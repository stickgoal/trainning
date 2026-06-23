package me.maiz.rocketmqdemo.messagetype.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyTransactionListener implements TransactionListener {
    //临时存储事务状态，真实情况存入数据库
    private AtomicInteger transactionIndex = new AtomicInteger(0);
    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

    /**
     *  半消息发送成功后的回调，在当中写本地事务逻辑
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        System.out.printf("开始执行业务，开启本地事务：%s，%s",msg,arg);
        //执行业务
        System.out.println(System.currentTimeMillis()+" 本地事务执行结束，存储本地事务结果并返回");
        int value = transactionIndex.getAndIncrement();
        int status = value % 3;
        localTrans.put(msg.getTransactionId(), status);
        //返回一个中间状态，触发后续MQ执行消息回查
        String tags = msg.getTags();
        switch (tags) {
            case "SUCCESS":
                System.out.println(msg+"执行本地事务成功，提交并投递");
                return LocalTransactionState.COMMIT_MESSAGE;
            case "FAIL":
                System.out.println(msg+"执行本地事务失败，回滚并取消");
                return LocalTransactionState.ROLLBACK_MESSAGE;
            case "UNKNOWN":
            case "UNKNOWN_SUCCESS":
            case "UNKNOWN_FAIL":
                System.out.println(msg+"未知，待回查");
                return LocalTransactionState.UNKNOW;
        }

        return LocalTransactionState.UNKNOW;

    }

    /**
     * 用于消息回查
     * @param msg
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        System.out.println(System.currentTimeMillis()+" 处理消息回查..."+msg);
        String tags = msg.getTags();
        switch (tags) {
            case "SUCCESS":
                System.out.println("回查成功，提交并投递");
                break;
            case "FAIL":
                System.out.println("回查失败，回滚并取消");
                break;
            case "UNKNOWN_SUCCESS":
                System.out.println("回查成功，提交并投递");
                return LocalTransactionState.COMMIT_MESSAGE;
            case "UNKNOWN_FAIL":
                System.out.println("回查失败，回滚并取消");
                return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }
}
