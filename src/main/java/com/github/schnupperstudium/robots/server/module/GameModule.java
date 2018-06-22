package com.github.schnupperstudium.robots.server.module;

import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;

public interface GameModule {
	void init(RobotsServer server, Game game);	
}
