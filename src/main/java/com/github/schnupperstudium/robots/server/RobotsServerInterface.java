package com.github.schnupperstudium.robots.server;

import java.util.List;

public interface RobotsServerInterface {
	public static final int NETWORK_ID = 100;
	
	default long joinGame(String name, String level) {
		return joinGame(name, level, null);
	}
	
	long joinGame(String name, String level, String auth);
	
	List<GameInfo> listRunningLevels();
	List<Level> listAvailableLevels();
	long startGame(String level, String auth);
}
