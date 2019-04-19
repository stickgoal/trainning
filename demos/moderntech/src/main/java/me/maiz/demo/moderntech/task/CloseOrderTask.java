package me.maiz.demo.moderntech.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloseOrderTask {

    Logger logger = LoggerFactory.getLogger("task.closeOrderTask");

    @Scheduled(cron="30 * * * * *")
    public void closeOrder(){
        logger.info("执行关闭订单任务");
    }

}
