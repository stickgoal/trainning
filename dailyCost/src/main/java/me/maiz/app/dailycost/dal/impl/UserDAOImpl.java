package me.maiz.app.dailycost.dal.impl;

import me.maiz.app.dailycost.dal.UserDAO;
import me.maiz.app.dailycost.dal.model.User;
import me.maiz.app.dailycost.enums.DailyCostResultCode;
import me.maiz.app.dailycost.exception.AppException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * Created by Lucas on 2017-01-17.
 */
@Repository("userDAO")
public class UserDAOImpl extends BaseDAO<User> implements UserDAO {


    @Override
    public void save(User user) {
        super.save(user);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        try {
            User user = getSession()
                    .createQuery("From User where username=:username and password =:password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return user;
        } catch (DataAccessException e) {
            throw new AppException(DailyCostResultCode.DATA_ACCESS_FAIL);
        }
    }
}
