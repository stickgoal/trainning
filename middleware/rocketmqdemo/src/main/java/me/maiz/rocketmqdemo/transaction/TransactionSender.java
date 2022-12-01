package me.maiz.rocketmqdemo.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

public class TransactionSender {


    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, InterruptedException {

        TransactionMQProducer txProducer = new TransactionMQProducer("tx_msg_test_producer_group");
        txProducer.setNamesrvAddr("localhost:9876");
        //设置MQ回调监听
        txProducer.setTransactionListener(new MyTransactionListener());
        //设置线程池，用于异步的消息检查？
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        txProducer.setExecutorService(executorService);

        //启动
        txProducer.start();

        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        //发送10条消息
        for (int i = 0; i < 10; i++) {
            try {
                Message msg =
                        new Message("TopicTxTest", tags[i % tags.length], "KEY" + i,
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));


                TransactionSendResult sendResult = txProducer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);
                //等一会儿再发下一条
                Thread.sleep(10);
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //事务监听器是异步执行的，所以需要等
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        txProducer.shutdown();
        System.out.println("发送者关闭");
    }
}
