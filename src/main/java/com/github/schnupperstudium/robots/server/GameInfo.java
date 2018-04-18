package com.github.schnupperstudium.robots.server;

public class GameInfo {
	private final long uuid;
	private final String name;
	private final Level level;
	private final boolean password;
	
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
