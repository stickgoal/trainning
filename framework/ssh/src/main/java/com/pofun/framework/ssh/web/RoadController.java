package com.pofun.framework.ssh.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pofun.framework.ssh.dal.model.Road;
import com.pofun.framework.ssh.service.RoadService;

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
