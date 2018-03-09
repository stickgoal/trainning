package me.maiz.trainning.framework.spring.anno;

import me.maiz.trainning.framework.spring.xml.User;

/**
 * Created by Lucas on 2017-01-10.
 */
public interface UserDAO {

    public User findById(int id);

}
