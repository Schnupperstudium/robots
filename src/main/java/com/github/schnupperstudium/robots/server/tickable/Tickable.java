package com.github.schnupperstudium.robots.server.tickable;

import com.github.schnupperstudium.robots.server.Game;

/**
 * Tickables get periodic updates during the game. This allows them to modify 
 * the game at specific times.
 * 
 * @author Daniel Wieland
 *
 */
public interface Tickable {
	/**
	 * The phase in which this wants to get updated.
	 * 
	 * @return phase for updates.
	 */
	TickableType getTickableType();
	
	/**
	 * Performs all updates to the passed game.
	 * 
	 * @param game game to be modified.
	 */
	void update(Game game);
}
