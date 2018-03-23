package me.maiz.trainning.framework.ssm.dal.impl;

import me.maiz.trainning.framework.ssm.dal.RoadDAO;
import me.maiz.trainning.framework.ssm.dal.mapper.RoadMapper;
import me.maiz.trainning.framework.ssm.dal.model.Road;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("roadDAO")
public class RoadDAOImpl implements RoadDAO {

	@Autowired
	private RoadMapper roadMapper;

	@Override
	public void save(Road road) {
		roadMapper.insert(road);
	}
	

}
