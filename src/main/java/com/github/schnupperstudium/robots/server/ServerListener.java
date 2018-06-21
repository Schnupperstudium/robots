package com.github.schnupperstudium.robots.server;

public interface ServerListener {
	void onGameStart(RobotsServer server, Game game);
	boolean canGameStart(RobotsServer server, Game game);
	void onGameEnd(RobotsServer server, Game game);
}
