package me.maiz.app.dailycost.service.impl;

import me.maiz.app.dailycost.common.MailUtil;
import me.maiz.app.dailycost.service.MailService;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service("mailService")

public class MailServiceImpl implements MailService
{

    private  String resetPasswordMailContent="<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>重置密码邮件</title>\n" +
            "    <style>\n" +
            "        body{\n" +
            "            background: #eee;\n" +
            "            font-family: \"微乳雅黑\",\"tahoma\",\"Helvetica Neue\";\n" +
            "            font-size:16px;\n" +
            "            color:#333;\n" +
            "        }\n" +
            "        #greeting{\n" +
            "\n" +
            "        }\n" +
            "        #content{\n" +
            "            padding-left:2em ;\n" +
            "        }\n" +
            "        #sign{\n" +
            "            padding-top: 2em;\n" +
            "            padding-left: 30em;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<p id=\"greeting\">亲爱的username，</p>\n" +
            "<br/>\n" +
            "<div id=\"content\">您在乐记申请的重置密码的验证码为：<code>captchaCode<code>,请妥善保管!\uD83D\uDE0A</div>\n" +
            "<div id=\"sign\">乐记助手</div>\n" +
            "</body>\n" +
            "</html>";


    @Override
    public void sendResetPasswordMail(String to) {
        resetPasswordMailContent = resetPasswordMailContent.replace("username",to);
        resetPasswordMailContent = resetPasswordMailContent.replace("captchaCode",to);
        try {
            MailUtil.send(to,"您正在申请重置乐记密码",resetPasswordMailContent);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
