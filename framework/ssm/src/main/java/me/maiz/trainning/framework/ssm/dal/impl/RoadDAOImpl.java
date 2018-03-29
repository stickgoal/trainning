package me.maiz.trainning.framework.ssm.dal.impl;

import me.maiz.trainning.framework.ssm.dal.RoadDAO;
import me.maiz.trainning.framework.ssm.dal.mapper.BlogMapper;
import me.maiz.trainning.framework.ssm.dal.mapper.RoadMapper;
import me.maiz.trainning.framework.ssm.dal.model.Blog;
import me.maiz.trainning.framework.ssm.dal.model.Road;
import me.maiz.trainning.framework.ssm.dal.model.RoadExample;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("roadDAO")
public class RoadDAOImpl implements RoadDAO {

	@Autowired
	private RoadMapper roadMapper;

	@Autowired
	private BlogMapper blogMapper;

	private static Logger logger = LoggerFactory.getLogger(RoadDAOImpl.class);

	@Override
	public void save(Road road) {
		logger.debug("","");

		roadMapper.insert(road);

		Blog b = new Blog();
		b.setState("A");
		b.setAuthorName("A");
		b.setFeatured((short)1);
		b.setTitle("A");
		blogMapper.insert(b);


		roadMapper.selectByExampleWithRowbounds(new RoadExample(),new RowBounds(0,5));
	}
	

}
