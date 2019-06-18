package me.maiz.trainning.framework.spring.component;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class NoticeArrivedEvent extends ApplicationEvent {

    private String subject;

    private String content;


    public NoticeArrivedEvent(Object source,String subject,String content){
        super(source);
        this.subject=subject;
        this.content=content;
    }

    public NoticeArrivedEvent(ApplicationContext source) {
        super(source);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NoticeArrivedEvent{" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}
