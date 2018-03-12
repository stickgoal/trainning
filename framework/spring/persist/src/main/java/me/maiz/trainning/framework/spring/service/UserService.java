package me.maiz.trainning.framework.spring.service;

import me.maiz.trainning.framework.spring.model.User;

/**
 * Created by Lucas on 2018-03-09.
 */
public interface UserService {

    void registerDeclaration(User user);

    void registerProgrammatic(User user);


}
