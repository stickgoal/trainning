package me.maiz.trainning.framework.ssm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.maiz.trainning.framework.ssm.dal.RoadDAO;
import me.maiz.trainning.framework.ssm.dal.model.Road;
import me.maiz.trainning.framework.ssm.service.RoadService;

@Service("roadService")
public class RoadServiceImpl implements RoadService {

	@Autowired
	private RoadDAO roadDAO;
	
	
	@Transactional
	public void register(Road road) {
		roadDAO.save(road);
	}

}
