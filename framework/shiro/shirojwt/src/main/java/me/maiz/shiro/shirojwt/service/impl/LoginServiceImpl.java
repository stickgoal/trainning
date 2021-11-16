package me.maiz.shiro.shirojwt.service.impl;

import me.maiz.shiro.shirojwt.model.User;
import me.maiz.shiro.shirojwt.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public User login(String username, String password) {
        User user = new User();
        user.setUserId(1);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
