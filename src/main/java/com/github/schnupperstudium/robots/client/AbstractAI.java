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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (entityUUID ^ (entityUUID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAI other = (AbstractAI) obj;
		if (entityUUID != other.entityUUID)
			return false;
		return true;
	}
}
