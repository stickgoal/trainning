package me.maiz.app.dailycost.dal.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDAO<T> {

    @Autowired
    protected SessionFactory sessionFactory;

	protected Session getSession(){
		return  sessionFactory.getCurrentSession();

	}

	protected void save(T t){
		getSession().save(t);
	}
	
}
