package me.maiz.middleware.rocketmqbootdemo.sending;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

/**
 * 事务消息监听器
 * 用于处理事务消息的本地事务执行和状态回查
 */
@Slf4j
@RocketMQTransactionListener
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    /**
     * 执行本地事务
     * @param message 消息内容
     * @param arg 业务参数
     * @return 事务状态：COMMIT、ROLLBACK、UNKNOWN
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        log.info("开始执行本地事务");
        
        try {
            // 在这里执行业务逻辑，如数据库操作
            // 例如：保存订单信息到数据库
            
            log.info("本地事务执行成功");
            return RocketMQLocalTransactionState.COMMIT;
            
        } catch (Exception e) {
            log.error("本地事务执行失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务状态（当 Broker 未收到确认时进行回查）
     * @param message 消息内容
     * @return 事务状态：COMMIT、ROLLBACK、UNKNOWN
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        log.info("开始回查本地事务状态");
        
        // 根据业务情况判断事务状态
        // 例如：查询数据库中是否存在对应的订单记录
        
        // 这里简化处理，实际应根据业务逻辑判断
        return RocketMQLocalTransactionState.COMMIT;
    }
}
