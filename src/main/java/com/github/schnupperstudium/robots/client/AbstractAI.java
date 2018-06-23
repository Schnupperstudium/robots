package com.github.schnupperstudium.robots.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

/**
 * A basic AI.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class AbstractAI {
	private final RobotsClient client;
	private final long gameId;
	private final long entityUUID;
	private final List<EntityObserver> entityObservers = new ArrayList<>();
	private final List<VisionObserver> visionObservers = new ArrayList<>();
	
	private Entity entity;
	private List<Tile> vision;

	public AbstractAI(RobotsClient client, long gameId, long entityUUID) {
		this.client = client;
		this.gameId = gameId;
		this.entityUUID = entityUUID;
	}
	
	/**
	 * This method is used by the framework to update the controlled entity.
	 * 
	 * @param entity updated entity
	 */
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
	
	/**
	 * This method is used by the framework to update the vision.
	 * 
	 * @param tiles new visable tiles
	 */
	public void updateVision(List<Tile> tiles) {
		this.vision = new ArrayList<>(tiles);
		
		synchronized (visionObservers) {
			visionObservers.forEach(o -> o.onVisionUpdate(this, new ArrayList<>(tiles)));
		}
	}
	
	/**
	 * @return facing of the entity.
	 */
	public Facing getFacing() {
		final Entity e = getEntity();
		if (e == null)
			return null;
		else
			return e.getFacing();
	}
	
	/**
	 * Searches for the neighboring tile in the given direction.
	 * If there is no tile found it will create a temporary tile with the needed coordinates 
	 * and <code>Material.VOID</code> as material.
	 * 
	 * @param facing direction to search in.
	 * @return tile in the given direction.
	 */
	public Tile getTileByFacing(Facing facing) {
		final int x = getEntity().getX() + facing.dx;
		final int y = getEntity().getY() + facing.dy;
		
		for (Tile tile : getVision()) {
			if (tile.getX() == x && tile.getY() == y)
				return tile;
		}
		
		return new Tile(null, x, y, Material.VOID);
	}
	
	public Tile getBeneathTile() {
		final int x = getEntity().getX();
		final int y = getEntity().getY();
		
		for (Tile tile : getVision()) {
			if (tile.getX() == x && tile.getY() == y)
				return tile;
		}
		
		return new Tile(null, x, y, Material.VOID);
	}
	
	/**
	 * Tile to the left of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getLeftTile() {
		return getTileByFacing(getEntity().getFacing().left());
	}
	
	/**
	 * Tile in front of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getFrontTile() {
		return getTileByFacing(getEntity().getFacing());
	}
	
	/**
	 * Tile to the right of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getRightTile() {
		return getTileByFacing(getEntity().getFacing().right());
	}
	
	/**
	 * Tile behind of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getBackTile() {
		return getTileByFacing(getEntity().getFacing().opposite());
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

	/**
	 * @return entity x coordinate.
	 */
	public int getX() {
		return entity.getX();
	}
	
	/**
	 * @return entity y coordinate.
	 */
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
	
	/**
	 * Removes this AI from the game.
	 * 
	 * @return true if successful.
	 */
	public boolean despawn() {
		return client.despawnAI(this);
	}
	
	/**
	 * @return the client this AI was created with.
	 */
	public RobotsClient getClient() {
		return client;
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
