package me.maiz.trainning.boot.bootmq.config;

import me.maiz.trainning.boot.bootmq.components.Receiver;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "bootmq-exchange";

    public static final String QUEUE_NAME = "order.delivery.queue";

    /**
     * 声明一个Queue，第二个参数表示是否是持久化的
     * @return
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME,true);
    }

    /**
     * 声明一个交换器
     * @return
     */
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    /**
     * 声明一个绑定，将队列绑定到一个交换器上，routingKey用于路由数据
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding binding(Queue queue,TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("order.delivery.queue");
    }

    //参考自 https://blog.csdn.net/u013871100/article/details/82982235
    //通过注解@RabbitListener配置监听器
   /* @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(CachingConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
*/


/*  spring官方教程使用方式，可以支持pojo注解监听
    *//**
     * 声明一个消息监听的容器，用于控制产生多少个监听器，解决并发问题
     * @param connectionFactory  starter自动提供
     * @param listenerAdapter    监听适配器
     * @return
     *//*
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);

        container.setMessageListener(listenerAdapter);
        return container;
    }

    *//**
     * 监听适配器，这里用反射的方式将消息接受功能适配到POJO上，第二个参数是方法名字
     * @param receiver
     * @return
     *//*
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "recieve");
    }*/

}
