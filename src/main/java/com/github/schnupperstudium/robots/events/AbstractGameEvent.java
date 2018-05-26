package com.github.schnupperstudium.robots.events;

import com.github.schnupperstudium.robots.server.Game;

public abstract class AbstractGameEvent extends AbstractExecutableEvent<Game> {
	public AbstractGameEvent() {
		
	}
	
	public AbstractGameEvent(boolean canCancel) {
		super(canCancel);
	}
}
