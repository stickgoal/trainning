package me.maiz.trainning.framework.spring.service.impl;


import me.maiz.trainning.framework.spring.component.NoticeArrivedEvent;
import me.maiz.trainning.framework.spring.dao.UserDAO;
import me.maiz.trainning.framework.spring.dao.model.User;
import me.maiz.trainning.framework.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("userService")
public class UserServiceImpl implements UserService, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserDAO userDAO;

    @Override
    public String login(String username, String password) {

        User user = userDAO.findByUsernameAndPassword(username,password);
        applicationEventPublisher.publishEvent(new NoticeArrivedEvent(this,"用户"+username+"登录成功","用户名："+username+" 时间："+new Date()));
        return user!=null?"成功":"失败";
    }

    public void setUserDAO(UserDAO userDAO) {
        System.out.println("调用setUserDAO");
        this.userDAO = userDAO;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher= applicationEventPublisher;
    }
}
