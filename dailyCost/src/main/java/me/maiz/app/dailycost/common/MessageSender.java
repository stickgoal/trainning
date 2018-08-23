package me.maiz.app.dailycost.common;

import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("messageSender")
public class MessageSender {

        private final Logger logger = LoggerFactory.getLogger(getClass());
        // 用户账号
        private String name="YOUR_ACCOUNT";
        // 发送内容的模板
        private String contentTemplate;
        // 密码
        private String pwd="YOUR_PASSWORD";
        // 签名
        private String sign="YOUR_SIGN";
        // 发送请求的url地址
        private String url="http://web.duanxinwang.cc/asmx/smsservice.aspx";
        // 短信类型
        private String type="pt";

        @Autowired
        private RestTemplate restTemplate;

        public String getContentTemplate() {
            return contentTemplate;
        }


        public void sendMessage(final String phone) {


            ResponseEntity<String> res = null;
            try {
                StringBuilder sBuilder = new StringBuilder(url);
                sBuilder.append("?content=").append("您的验证码为2345").append("&name=").append(name).append("&pwd=").append(pwd)
                        .append("&type=").append(type).append("&sign=").append(sign).append("&mobile=").append(phone);
                res = restTemplate.postForEntity(sBuilder.toString(), null, String.class);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
            logger.info("{}",res);


            String body = res.getBody();
            if (logger.isDebugEnabled()) {
                logger.info(body);
            }

        }


        public void setContentTemplate(String contentTemplate) {
            this.contentTemplate = contentTemplate;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public void setRestTemplate(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


