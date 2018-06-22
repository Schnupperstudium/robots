package com.github.schnupperstudium.robots.server.event;

import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;

public interface ServerListener {
	void onGameStart(RobotsServer server, Game game);
	boolean canGameStart(RobotsServer server, Game game);
	void onGameEnd(RobotsServer server, Game game);
}
