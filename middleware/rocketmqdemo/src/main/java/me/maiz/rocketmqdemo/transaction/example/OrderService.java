package me.maiz.rocketmqdemo.transaction.example;

public interface OrderService {
    /**
     * 发送半消息到MQ，准备开始事务
     * @param order
     */
    void prepareCreateOrder(Order order);

    /**
     * 半消息成功后，执行本地事务
     * @param order
     * @param txId
     */
    void executeCreateOrderLocally(Order order,int txId);
}
