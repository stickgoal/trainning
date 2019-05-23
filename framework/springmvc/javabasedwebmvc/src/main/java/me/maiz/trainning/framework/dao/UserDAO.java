package me.maiz.trainning.framework.dao;

import me.maiz.trainning.framework.dao.model.User;

public interface UserDAO {

    User findById(int userId);
}
