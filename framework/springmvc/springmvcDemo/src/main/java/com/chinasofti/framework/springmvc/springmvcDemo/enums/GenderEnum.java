package com.chinasofti.framework.springmvc.springmvcDemo.enums;

public enum GenderEnum {
	M("M", "男"), F("F", "女");

	private String name;
	private String message;

	private GenderEnum(String name, String message) {
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
