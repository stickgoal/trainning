package me.maiz.trainning.framework.spring.injection.service;

import me.maiz.trainning.framework.spring.injection.model.User;

/**
 * Created by Lucas on 2018-03-09.
 */
public interface UserService {

    User findByName(String name);


}
