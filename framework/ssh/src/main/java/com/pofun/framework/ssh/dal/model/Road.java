package com.pofun.framework.ssh.dal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tjs_road")
public class Road {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private long roadId;
	
	@Column(name="road_name")
	private String roadName;

	public long getRoadId() {
		return roadId;
	}

	public void setRoadId(long roadId) {
		this.roadId = roadId;
	}

	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	@Override
	public String toString() {
		return "Road [roadId=" + roadId + ", roadName=" + roadName + "]";
	}

}
