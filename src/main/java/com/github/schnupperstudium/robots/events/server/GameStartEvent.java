package com.github.schnupperstudium.robots.events.server;

import com.github.schnupperstudium.robots.events.AbstractServerEvent;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;

public class GameStartEvent extends AbstractServerEvent {
	private final Game game;
	
	public GameStartEvent(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}
	
	@Override
	protected boolean apply(RobotsServer target) {
		target.addGame(game);
		return true;
	}
}
