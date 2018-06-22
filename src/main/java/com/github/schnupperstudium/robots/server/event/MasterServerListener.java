package com.github.schnupperstudium.robots.server.event;

import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;

public class MasterServerListener extends MasterListener<ServerListener> implements ServerListener {

	public MasterServerListener() {

	}
	
	@Override
	public void onGameStart(RobotsServer server, Game game) {
		notifyListeners(l -> l.onGameStart(server, game));
	}

	@Override
	public boolean canGameStart(RobotsServer server, Game game) {
		return consultListeners(l -> l.canGameStart(server, game));
	}
	
	@Override
	public void onGameEnd(RobotsServer server, Game game) {
		notifyListeners(l -> l.onGameEnd(server, game));
	}
}
