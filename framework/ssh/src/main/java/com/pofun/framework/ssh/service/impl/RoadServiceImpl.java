package com.pofun.framework.ssh.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pofun.framework.ssh.dal.RoadDAO;
import com.pofun.framework.ssh.dal.model.Road;
import com.pofun.framework.ssh.service.RoadService;

@Service("roadService")
public class RoadServiceImpl implements RoadService {

	@Autowired
	private RoadDAO roadDAO;
	
	
	@Transactional
	public void register(Road road) {
		roadDAO.save(road);
	}

}
