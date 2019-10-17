package me.maiz.trainning.framework.ssm.web;

import me.maiz.trainning.framework.ssm.dal.model.Road;
import me.maiz.trainning.framework.ssm.service.RoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RoadController {
	
	@Autowired
	private RoadService roadService;

	@RequestMapping("addRoad")
	public String add(String roadName){
		Road road = new Road();
		road.setRoadName(roadName);
		roadService.register(road);



		return "addSuccess";
	}
	
}
