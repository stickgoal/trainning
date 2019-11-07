package me.maiz.trainning.framework.ssm.dal;

import me.maiz.trainning.framework.ssm.dal.model.User;

public interface UserDAO {

    User findUserByUsernameAndPassword(String username,String password);

}
