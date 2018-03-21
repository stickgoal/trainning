package com.pofun.framework.ssh.dal.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pofun.framework.ssh.dal.RoadDAO;
import com.pofun.framework.ssh.dal.model.Road;

@Repository("roadDAO")
public class RoadDAOImpl implements RoadDAO{

	@Autowired
	private SessionFactory sessionFactory; 
	
	
	@Override
	public void save(Road road) {
		sessionFactory.getCurrentSession().save(road);
	}
	

}
