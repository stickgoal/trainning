package com.chinasofti.ee.mailDemo.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {

    private static final String OWNER_NAME = "YOUR_OWN_EMAIL_ADDRESS";

    private static final String PASSWORD = "YOUR_OWN_EMAIL_PASSWORD";

    private static final String MAIL_HOST = "smtp.163.com";

    public static final String PROTOCOL = "smtp";


    public static void send(String to,String subject,String content) throws MessagingException {
        Properties prop=new Properties();
        prop.put("mail.host",MAIL_HOST );
        prop.put("mail.transport.protocol", PROTOCOL);
        prop.put("mail.smtp.auth", true);
        //使用java发送邮件5步骤
        //1.创建sesssion
        Session session=Session.getInstance(prop);
        //开启session的调试模式，可以查看当前邮件发送状态
        session.setDebug(true);

        //2.通过session获取Transport对象（发送邮件的核心API）
        Transport ts=session.getTransport();
        //3.通过邮件用户名密码链接
        ts.connect(OWNER_NAME, PASSWORD);

        //4.创建邮件
        Message msg=createSimpleMail(session,to,subject,content);

        //5.发送电子邮件
        ts.sendMessage(msg, msg.getAllRecipients());
    }

    public static MimeMessage createSimpleMail(Session session,String to,String subject,String content) throws AddressException,MessagingException {
        //创建邮件对象
        MimeMessage mm = new MimeMessage(session);
        //设置发件人
        mm.setFrom(new InternetAddress(OWNER_NAME));
        //设置收件人
        mm.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        //设置抄送人
//        mm.setRecipient(Message.RecipientType.CC, new InternetAddress(""));
        //设置邮件标题
        mm.setSubject(subject);
        //设置正文内容
        mm.setContent(content, "text/html;charset=utf-8");

        return mm;
    }
    }
