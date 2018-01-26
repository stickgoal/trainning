package me.maiz.app.dailycost.dal;

import me.maiz.app.dailycost.dal.model.User;

/**
 * Created by Lucas on 2017-01-17.
 */
public interface UserDAO {

    void save(User user);

    User findByUsernameAndPassword(String username,String password);

}
