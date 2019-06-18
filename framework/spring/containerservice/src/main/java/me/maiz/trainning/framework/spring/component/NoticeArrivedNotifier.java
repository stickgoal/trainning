package me.maiz.trainning.framework.spring.component;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NoticeArrivedNotifier implements ApplicationListener<NoticeArrivedEvent> {

    @Override
    public void onApplicationEvent(NoticeArrivedEvent noticeArrivedEvent) {
        //接收到请求
        System.out.println(noticeArrivedEvent);
    }
}
