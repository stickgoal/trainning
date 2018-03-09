package me.maiz.trainning.framework.spring.injection.dal;

import me.maiz.trainning.framework.spring.injection.model.User;

/**
 * Created by Lucas on 2018-03-09.
 */
public interface UserDAO {

    User findByName(String name);

}
