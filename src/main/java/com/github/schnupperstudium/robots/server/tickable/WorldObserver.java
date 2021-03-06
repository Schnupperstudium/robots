package com.github.schnupperstudium.robots.server.tickable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.server.Game;

public class WorldObserver implements Tickable {
	private static Logger LOG = LogManager.getLogger();
	
	private final RobotsClientInterface clientInterface;
	
	public WorldObserver(RobotsClientInterface clientInterface) {
		this.clientInterface = clientInterface;
	}

	@Override
	public void update(Game game) {
		try {
			clientInterface.updateWorld(game.getUUID(), game.getWorld());
		} catch (Exception e) {
			// catch any exception a client may cause
			LOG.warn("observer got kicked from {}:{} (Reason: {})", game.getName(), game.getUUID(), e.getMessage());
			game.removeTickable(this);
		}
	}
	
	@Override
	public TickableType getTickableType() {
		return TickableType.MONITOR;
	}
	
	public RobotsClientInterface getClientInterface() {
		return clientInterface;
	}
}
