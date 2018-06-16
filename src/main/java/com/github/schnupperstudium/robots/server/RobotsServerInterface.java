package com.github.schnupperstudium.robots.server;

import java.util.List;

import com.github.schnupperstudium.robots.entity.LivingEntity;

/**
 * Interface to interact with the server.
 * 
 * @author Daniel Wieland
 *
 */
public interface RobotsServerInterface {
	public static final int NETWORK_ID = 100;

	/**
	 * @return info about all running games.
	 */
	List<GameInfo> listGames();
	/**
	 * @return all available levels.
	 */
	List<Level> listLevels();
	
	/**
	 * Requests the server to start a level. If the returned id is less or equal to 0 it's not valid 
	 * and the server could not start the game or did refuse to do so.
	 * 
	 * @param name name of the game
	 * @param level level to use (it's name)
	 * @param auth authentication token to join a game or <code>null</code>
	 * @return the games id
	 */
	long startGame(String name, String levelName, String auth);
	
	/**
	 * Attempts to spawn a robot in the game. If the returned id is less or equal to 0 it's 
	 * not valid and the spawn failed.
	 * 
	 * @param gameId game to spawn in
	 * @param name name of the robot
	 * @param auth authentication token for the game or <code>null</code>.
	 * @return robots id
	 */
	long spawnEntity(long gameId, String name, String auth);
	
	/**
	 * Attempts to spawn a {@link LivingEntity} in the game. If the returned id is less or equal to 0 it's 
	 * not valid and the spawn failed.
	 * 
	 * @param gameId game to spawn in
	 * @param name name of the robot
	 * @param auth authentication token for the game or <code>null</code>.
	 * @param entityType string representation of entities class.
	 * @return entity id
	 */
	long spawnEntity(long gameId, String name, String auth, String entityType);
	
	
	/**
	 * Remove the given entity from the given game.
	 * 
	 * @param gameId game id
	 * @param entityUUID entity id
	 */
	boolean removeEntity(long gameId, long entityUUID);
	
	/**
	 * Attempts start observing the game. 
	 * 
	 * @param gameId game to observe
	 * @param auth authentication token for the game
	 * @return true if observing started, false otherwise.
	 */
	boolean observerWorld(long gameId, String auth);
	
	/**
	 * Remove the observer from the given game.
	 * 
	 * @param gameId game id
	 */
	boolean stopObserving(long gameId);
}
