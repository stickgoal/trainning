package me.maiz.trainning.framework.spring.dal.impl;

import me.maiz.trainning.framework.spring.dal.UserDAO;
import me.maiz.trainning.framework.spring.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * Created by Lucas on 2018-03-12.
 */
@Repository("hbUserDAO")
public class HibernateUserDAOImpl extends HibernateDaoSupport implements UserDAO {


    @Autowired
    public void set(SessionFactory sessionFactory){
        this.setSessionFactory(sessionFactory);
    }


    public void save(User user) {
        getHibernateTemplate().save(user);
    }
}
