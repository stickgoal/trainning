package me.maiz.shiro.shirojwt.service;

import me.maiz.shiro.shirojwt.model.User;

public interface LoginService {

    User login(String username, String password);

}
