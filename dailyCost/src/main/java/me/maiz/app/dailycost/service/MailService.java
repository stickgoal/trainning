package me.maiz.app.dailycost.service;

import org.springframework.stereotype.Service;

public interface MailService{


    void sendResetPasswordMail(String to);

}
