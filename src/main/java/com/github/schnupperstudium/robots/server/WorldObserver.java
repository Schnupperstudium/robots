package com.github.schnupperstudium.robots.server;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;

public class WorldObserver implements Tickable {
	private final RobotsClientInterface clientInterface;
	
	public WorldObserver(RobotsClientInterface clientInterface) {
		this.clientInterface = clientInterface;
	}

	@Override
	public void update(Game game) {
		clientInterface.updateWorld(game.getUUID(), game.getWorld());
	}
}
