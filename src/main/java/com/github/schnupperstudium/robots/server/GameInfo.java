package com.github.schnupperstudium.robots.server;

public class GameInfo {
	private long uuid;
	private String name;
	private Level level;
	private boolean password;
	
	protected GameInfo() {
		// constructor for kryo
	}
	
	public GameInfo(long uuid, String name, Level level, boolean password) {
		this.uuid = uuid;
		this.name = name;
		this.level = level;
		this.password = password;
	}
	
	public long getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public boolean hasPassword() {
		return password;
	}
}
