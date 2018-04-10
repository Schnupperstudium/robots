package com.github.schnupperstudium.robots.server;

public class GameInfo {
	private final long uuid;
	private final Level level;
	private final boolean password;
	
	public GameInfo(long uuid, Level level, boolean password) {
		this.uuid = uuid;
		this.level = level;
		this.password = password;
	}
	
	public long getUUID() {
		return uuid;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public boolean hasPassword() {
		return password;
	}
}
