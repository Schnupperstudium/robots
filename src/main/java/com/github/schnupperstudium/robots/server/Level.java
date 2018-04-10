package com.github.schnupperstudium.robots.server;

public class Level {
	private final String name;
	private final String location;
	private final String desc;
	
	public Level(String name, String location, String desc) {
		this.name = name;
		this.location = location;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getDesc() {
		return desc;
	}
}
