package me.maiz.app.dailycost.service;

import me.maiz.app.dailycost.dal.model.User;
import me.maiz.app.dailycost.web.form.RegForm;

/**
 * Created by Lucas on 2017-01-17.
 */
public interface UserService {
    /**
     * 保存
     * @param user
     */
    void save(User user);

    /**
     * 登录
     * @param username
     * @param password
     */
    User login(String username,String password);

    /**
     * 注册
     * @param regForm
     */
    void reg(RegForm regForm);
}
