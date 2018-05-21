package com.github.schnupperstudium.robots.client;

import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public interface RobotsClientInterface {
	public static final int NETWORK_ID = 1001;
	
	/**
	 * Notifies the client to update the entity with the provided uuid with the data provided.
	 * 
	 * @param uuid entity id
	 * @param entity updated entity instance
	 */
	void updateEntity(long uuid, Entity entity);
	/**
	 * Notifies the client to update the entities vision with the provided uuid with the data provided.
	 * 
	 * @param uuid entity uuid
	 * @param tiles visible tiles
	 */
	void updateVisableTiles(long uuid, List<Tile> tiles);
	/**
	 * Compute the turn for the entity with the provided id.
	 * 
	 * @param uuid uuid of the entity
	 * @return the action the entity takes
	 */
	EntityAction makeTurn(long uuid);
	
	/**
	 * Update to the world within the given game.
	 * 
	 * @param uuid game id
	 * @param world updated world instance
	 */
	void updateWorld(long uuid, World world);
}
