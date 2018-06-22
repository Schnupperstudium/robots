package com.github.schnupperstudium.robots.server.event;

import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;

public abstract class AbstractServerListener implements ServerListener {

	public AbstractServerListener() {

	}
	
	@Override
	public void onGameStart(RobotsServer server, Game game) { }

	@Override
	public boolean canGameStart(RobotsServer server, Game game) {
		return true;
	}
	
	@Override
	public void onGameEnd(RobotsServer server, Game game) { }

}
