package me.maiz.demos.wsdemo.config;

import lombok.extern.slf4j.Slf4j;
import me.maiz.demos.wsdemo.notification.NotificationController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 启动消息队列代理
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker( "/queue/");
    }

    /**
     * 注册一个STOMP端点，客户端可以连接到这个端点
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notify")
                .setHandshakeHandler(new DefaultHandshakeHandler(){
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

                        log.info("登入 ：{},{},{}",request,request.getHeaders(),attributes);
                        return super.determineUser(request, wsHandler, attributes);
                    }
                })
                .withSockJS();
    }
}
