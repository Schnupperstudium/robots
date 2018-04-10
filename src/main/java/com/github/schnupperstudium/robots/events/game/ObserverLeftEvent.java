package com.github.schnupperstudium.robots.events.game;

import com.github.schnupperstudium.robots.events.AbstractGameEvent;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.WorldObserver;

public class ObserverLeftEvent extends AbstractGameEvent {	
	private WorldObserver observer;
	
	public ObserverLeftEvent(WorldObserver observer) {
		this.observer = observer;
	}
	
	@Override
	protected boolean apply(Game game) {
		game.removeTickable(observer);
		return true;
	}
}
