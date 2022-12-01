package me.maiz.project.highconcurrency.web;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.maiz.project.highconcurrency.common.Result;
import me.maiz.project.highconcurrency.common.SecKillConstants;
import me.maiz.project.highconcurrency.model.Product;
import me.maiz.project.highconcurrency.model.Seckill;
import me.maiz.project.highconcurrency.service.IProductService;
import me.maiz.project.highconcurrency.service.ISeckillService;
import me.maiz.project.highconcurrency.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@Slf4j
public class SecKillQueryController {


    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private IProductService productService;

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private RedisService redisService;

    //返回一个渲染过后的页面
    @RequestMapping(value = "secKill", produces = "text/html")
    @ResponseBody
    public String secKill(HttpServletRequest request, HttpServletResponse response, Model model) {

        //1.取出redis中的页面
        String secKill = redisService.get(SecKillConstants.SECKILL_PAGE_KEY);

        if (null != secKill && secKill.length() > 0) {
            return secKill;
        }
        //2.redis中页面不存在则重新渲染
        log.info("数据不存在，渲染页面");
        //2.1取出商品信息
        final List<Product> products = productService.query(1);
        model.addAttribute("products",products);

        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //参数为模板页面
        final String html = thymeleafViewResolver.getTemplateEngine().process("secKill", ctx);

        //存入redis中
        if(html!=null&&html.length()>0){
            redisService.setExpireMinutes(SecKillConstants.SECKILL_PAGE_KEY,html,3);
        }

        return html;
    }


    //返回一个渲染过后的页面
    @RequestMapping(value = "secKillProduct/{productId}", produces = "text/html")
    @ResponseBody
    public String secKillProduct(@PathVariable("productId") int productId, HttpServletRequest request, HttpServletResponse response, Model model) {

        //1.取出redis中的页面
        final String productCacheKey = SecKillConstants.SECKILL_PRODUCT_KEY + "_" + productId;
        String secKill = redisService.get(productCacheKey);

        if (null != secKill && secKill.length() > 0) {
            return secKill;
        }
        //2.redis中页面不存在则重新渲染
        log.info("商品数据{}不存在，渲染页面",productId);
        //2.1取出商品信息
        final Product product = productService.getById(productId);
        model.addAttribute("p",product);

        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //参数为模板页面
        final String html = thymeleafViewResolver.getTemplateEngine().process("secKillProduct", ctx);

        //存入redis中
        if(html!=null&&html.length()>0){
            redisService.setExpireMinutes(productCacheKey,html,3);
        }

        return html;
    }

    @RequestMapping("getAddress")
    @ResponseBody
    public Result getAddress(int productId, HttpSession session){

        final Seckill secKill = seckillService.getSecKill(productId);

        int secKillId=1;

        final String addressKey = SecKillConstants.SECKILL_ADDRESS_KEY + productId;
        final String s = redisService.get(addressKey);
        if(s!=null&&s.length()>0){
            return Result.success(s);
        }

        if(new Date().getTime()>=(secKill.getStartTime().getTime())){
            //添加一个随机数作为token，以保证请求真实性，防伪造，可以改造成随机字符
            //调试方便，暂停token功能
//            final int token = new Random().nextInt(10000);
            String url = "/secKill/"+secKillId/*+"?token="+ token*/;
//            session.setAttribute("secKill_token_"+productId,token);
            redisService.setExpireMinutes(addressKey,url,60);
            return Result.success(url);
        }else{
            //尚未开始
            return Result.fail("NOT_STARTED");
        }


    }

}
