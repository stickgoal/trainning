package me.maiz.trainning.framework.spring.dal.impl;

import me.maiz.trainning.framework.spring.dal.OperationDAO;
import me.maiz.trainning.framework.spring.model.OperationLog;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * Created by Lucas on 2018-03-12.
 */
@Repository("hbOperatationLogDAO")
public class HibernateOperationLogDAOImpl extends HibernateDaoSupport implements OperationDAO {

    @Autowired
    public void set(SessionFactory sessionFactory){
        this.setSessionFactory(sessionFactory);
    }

    public void save(OperationLog log) {
        getHibernateTemplate().save(log);
    }
}
