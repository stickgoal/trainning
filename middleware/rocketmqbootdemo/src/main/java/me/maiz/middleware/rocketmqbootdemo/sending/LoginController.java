package me.maiz.middleware.rocketmqbootdemo.sending;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class LoginController {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @RequestMapping("/login")
    public String login(String username,String password){
        log.info("登录，用户名={}",username);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(12);
        userInfo.setUsername("weqeq");
        userInfo.setMobile("1333333333333");
        userInfo.setAvatar("asdfasdfasdf");
        userInfo.setRegTime(new Date());
        //同步发送
        SendResult sendResult = rocketMQTemplate.syncSend("login-success-topic",
                userInfo);
        log.info("发送成功：{}",sendResult);

        //异步发送
        /*rocketMQTemplate.asyncSend("login-success-topic",
                userInfo,
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("{}",sendResult);
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.warn("error",e);
                    }
                });*/
        return "success";
    }

}
