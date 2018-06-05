package com.github.schnupperstudium.robots.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.world.Tile;

/**
 * A basic AI with all basic features.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class AbstractAI {
	private final long gameId;
	private final long entityUUID;
	private final List<EntityObserver> entityObservers = new ArrayList<>();
	private final List<VisionObserver> visionObservers = new ArrayList<>();
	
	private Entity entity;
	private List<Tile> vision;

	
	public AbstractAI(long gameId, long entityUUID) {
		this.gameId = gameId;
		this.entityUUID = entityUUID;
	}
	
	public void updateEntity(Entity entity) {
		if (this.entity == null && entity != null)
			onEntitySpawn(entity);
		else if (this.entity != null && entity == null)
			onEntityDespawn(this.entity);
		else if (this.entity != null && entity != null && entity instanceof LivingEntity && this.entity instanceof LivingEntity) {
			LivingEntity currentEntity = (LivingEntity) this.entity;
			LivingEntity updatedEntity = (LivingEntity) entity;
			if (currentEntity.getCurrentHealth() <= 0 && updatedEntity.getCurrentHealth() > 0)
				onEntityRevive(updatedEntity);
			else if (currentEntity.getCurrentHealth() > 0 && updatedEntity.getCurrentHealth() <= 0)
				onEntityDeath(currentEntity);
		}
			
		this.entity = entity;
		
		synchronized (entityObservers) {
			entityObservers.forEach(o -> o.onEntityUpdate(this, entity));
		}
	}
	
	public void updateVision(List<Tile> tiles) {
		this.vision = new ArrayList<>(tiles);
		
		synchronized (visionObservers) {
			visionObservers.forEach(o -> o.onVisionUpdate(this, new ArrayList<>(tiles)));
		}
	}
	
	/**
	 * When this method is called the AI is supposed to compute an action to perform.
	 * 
	 * @return the action to be performed this turn.
	 */
	public abstract EntityAction makeTurn();
	
	/**
	 * @return the games uuid of this controlled entity.
	 */
	public long getGameId() {
		return gameId;
	}

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

	public int getX() {
		return entity.getX();
	}
	
	public int getY() {
		return entity.getY();		
	}
	
	/**
	 * Called when the entity spawns.
	 * 
	 * @param entity spawned entity
	 */
	protected void onEntitySpawn(Entity entity) {
		// may be overwritten by AI
	}
	
	/**
	 * Called when the ai despawns.
	 * 
	 * @param entity despawning entity
	 */
	protected void onEntityDespawn(Entity entity) {
		// may be overwritten by AI
	}
	
	/**
	 * Called when the entities hp go from below zero to above zero
	 * 
	 * @param entity entity
	 */
	protected void onEntityRevive(Entity entity) {
		// may be overwritten by AI
	}
	
	/**
	 * Called when the entities hp go from above zero to zero or below.
	 * 
	 * @param entity entity
	 */
	protected void onEntityDeath(Entity entity) {
		// may be overwritten by AI
	}
	
	public void addEntityUpdateObserver(EntityObserver entityObserver) {
		if (entityObserver == null)
			return;
		
		synchronized (entityObservers) {
			entityObservers.add(entityObserver);
		}
	}
	
	public void removeEntityUpdateObserver(EntityObserver entityObserver) {
		if (entityObserver == null)
			return;
		
		synchronized (entityObservers) {
			entityObservers.remove(entityObserver);
		}
	}
	
	public void clearEntityObservers() {
		synchronized (entityObservers) {
			entityObservers.clear();
		}
	}
	
	public void addVisionObserver(VisionObserver visionObserver) {
		if (visionObserver == null)
			return;
		
		synchronized (visionObservers) {
			visionObservers.add(visionObserver);
		}
	}
	
	public void removeVisionObserver(VisionObserver visionObserver) {
		if (visionObserver == null)
			return;
		
		synchronized (visionObservers) {
			visionObservers.remove(visionObserver);
		}
	}
	
	public void clearVisionObservers() {
		synchronized (visionObservers) {
			visionObservers.clear();
		}
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
