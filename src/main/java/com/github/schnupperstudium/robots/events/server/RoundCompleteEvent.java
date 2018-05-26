package com.github.schnupperstudium.robots.events.server;

import com.github.schnupperstudium.robots.events.AbstractGameEvent;
import com.github.schnupperstudium.robots.server.Game;

public class RoundCompleteEvent extends AbstractGameEvent {
	private final Game game;
	
	public RoundCompleteEvent(Game game) {
		super(false);
		
		this.game = game;
	}

	public Game getGame() {
		return game;
	}
	
	@Override
	protected boolean apply(Game target) {
		return true;
	}

}
