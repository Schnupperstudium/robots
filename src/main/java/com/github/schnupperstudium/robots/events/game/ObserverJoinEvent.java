package com.github.schnupperstudium.robots.events.game;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.events.AbstractGameEvent;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.WorldObserver;

public class ObserverJoinEvent extends AbstractGameEvent {	
	private WorldObserver observer;
	
	public ObserverJoinEvent(RobotsClientInterface clientInterface) {
		observer = new WorldObserver(clientInterface);
	}
	
	@Override
	protected boolean apply(Game game) {
		game.addTickable(observer);
		return true;
	}
}
