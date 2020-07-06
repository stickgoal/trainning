package me.maiz.project.highconcurrency.web;

import lombok.extern.slf4j.Slf4j;
import me.maiz.project.highconcurrency.common.Result;
import me.maiz.project.highconcurrency.common.SecKillConstants;
import me.maiz.project.highconcurrency.config.RabbitMqConfig;
import me.maiz.project.highconcurrency.model.Order;
import me.maiz.project.highconcurrency.model.Product;
import me.maiz.project.highconcurrency.model.SecKillOrder;
import me.maiz.project.highconcurrency.service.IOrderService;
import me.maiz.project.highconcurrency.service.IProductService;
import me.maiz.project.highconcurrency.service.ISeckillService;
import me.maiz.project.highconcurrency.service.RedisService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class SeckillController {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ISeckillService seckillService;


    /**
     * 秒杀商品的预加载,这里应该由定时任务驱动
     * @return
     */
    @Scheduled
    @RequestMapping("seckill/preload")
    @ResponseBody
    public Result preload(){

        int secKillId= 1;
        final List<Product> products = productService.query(1);
        for(Product p : products){
            String stockKey = SecKillConstants.SECKILL_PRODUCT_STOCK_KEY + p.getProductId();
            redisService.setExpireDays(stockKey,""+p.getStock(),2);
        }

        return Result.success();

    }

    /**
     * 秒杀操作
     *
     * @param productId
     * @param token
     * @return
     */
    @RequestMapping("secKill/{productId}")
    public String execute(@PathVariable("productId") int productId, Integer token, ModelMap modelMap, HttpSession session) {
        log.info("秒杀数据：{},{}",productId,token);
        //1.数据验证，暂停
        /*if (token == null) {
            modelMap.put("errorMsg", "请求不合法");
            return "secKillFail";
        }

        final String tokenKey = "secKill_token_" + productId;
        if (!token.equals(session.getAttribute(tokenKey))) {
            modelMap.put("errorMsg", "请求不合法");
            return "secKillFail";
        }

        session.removeAttribute(tokenKey);
        */

        //2.redis预处理 预减库存
        String stockKey = SecKillConstants.SECKILL_PRODUCT_STOCK_KEY + productId;

       List<Object> results =  redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public  List<Object> execute(RedisOperations operations) throws DataAccessException {
                //watch变量
                operations.watch(stockKey);
                //事务开启前先查询
                Object stock = operations.opsForValue().get(stockKey);

                //开启事务
                operations.multi();
                //必要的空查询，这个查询命令不会立即得到值
                // 而是加入命令队列，exec发出时才正式执行
                operations.opsForValue().get(stockKey);
                if (stock == null || Integer.valueOf((String)stock) <= 0) {
                    log.info("{}的redis库存已经为0",productId);
                    //取消事务
                    operations.discard();
                    return null;
                }
                //减少数据
                operations.opsForValue().decrement(stockKey);
                //提交事务
                return operations.exec();
            }
        });

        if(results!=null&&results.size()>0){
            log.info("redis预处理成功，进入mq");
            //3.mq异步处理
            rabbitTemplate.convertAndSend(RabbitMqConfig.QUEUE_NAME ,new SecKillOrder(productId,1,1,1));
            log.info("mq发送成功");
            return "secKillWait";
        }else{
            log.info("秒杀redis 处理结果：{}",results);
            return secKillFail("抱歉，您没抢到",modelMap);
        }


    }

    public String secKillFail(String message, ModelMap modelMap) {
        modelMap.put("errorMsg", message);
        return "secKillFail";
    }


    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void consume(SecKillOrder secKillOrder){
        log.info("[x]收到消息,{}",secKillOrder);
        seckillService.process(secKillOrder);

    }

    /**
     * 处理结果查询
     *
     * @return
     */
    @RequestMapping("secKillResult")
    @ResponseBody
    public Result resultQuery(Integer productId,HttpSession session,ModelMap modelMap) {
        //异步查询订单
        Integer userId = 1;//应当是从session中取出userId

        if(productId==null){
            return Result.fail("");
        }

         Order order = orderService.findByProductIdAndUserId(productId, userId);
        if(order==null){
            return Result.fail("暂未查询到订单,请稍候再试");
        }

        return Result.success("恭喜您，秒杀成功，订单号为"+order.getOrderId()+"请进入订单页面付款");
    }

}
