package com.github.schnupperstudium.robots.server;

public class Level {
	private final String name;
	private final String gameClass;
	private final String mapLocation;
	private final String desc;
	
	public Level(String name, String gameClass, String location, String desc) {
		this.name = name;
		this.gameClass = gameClass;
		this.mapLocation = location;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	
	public String getGameClass() {
		return gameClass;
	}
	
	public String getMapLocation() {
		return mapLocation;
	}
	
	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return "Level [name=" + name + ", gameClass=" + gameClass + ", mapLocation=" + mapLocation + ", desc=" + desc
				+ "]";
	}
}
