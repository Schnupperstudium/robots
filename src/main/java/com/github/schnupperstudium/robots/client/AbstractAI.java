package com.github.schnupperstudium.robots.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.world.Tile;

/**
 * A basic AI with all basic features.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class AbstractAI {
	private final long entityUUID;
	
	private Entity entity;
	private List<Tile> vision;

	public AbstractAI(long entityUUID) {
		this.entityUUID = entityUUID;
	}
	
	public void updateEntity(Entity entity) {
		this.entity = entity;
	}
	
	public void updateVision(List<Tile> tiles) {
		this.vision = new ArrayList<>(tiles);
	}
	
	/**
	 * When this method is called the AI is supposed to compute an action to perform.
	 * 
	 * @return the action to be performed this turn.
	 */
	public abstract EntityAction makeTurn();
	
	/**
	 * @return uuid of controlled entity.
	 */
	public long getEntityUUID() {
		return entityUUID;
	}
	
	/**
	 * @return current instance of controlled entity.
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * @return list containing the currently visible tiles.
	 */
	public List<Tile> getVision() {
		return vision;
	}
}
