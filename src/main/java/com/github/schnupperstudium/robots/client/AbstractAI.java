package com.github.schnupperstudium.robots.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.gui.client.ClientMapView;
import com.github.schnupperstudium.robots.world.Location;
import com.github.schnupperstudium.robots.world.Map;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
	
	private ClientMapView mapView;
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
	 * @return inventory of the entity.
	 */
	public Inventory getInventory() {
		final Entity e = getEntity();
		if (e == null)
			return null;
		else
			return e.getInventory();
	}
	
	/**
	 * Searches for the neighboring tile in the given direction.
	 * If there is no tile found it will create a temporary tile with the needed coordinates 
	 * and <code>Material.UNDEFINED</code> as material.
	 * 
	 * @param facing direction to search in.
	 * @return tile in the given direction.
	 */
	public Tile getTileByFacing(Facing facing) {
		return getTileByFacing(facing, 1);
	}
	
	/**
	 * Searches for the neighboring tile in the given direction and the given distance.
	 * If there is no tile found it will create a temporary tile with the needed coordinates 
	 * and <code>Material.UNDEFINED</code> as material.
	 * 
	 * @param facing direction to search in.
	 * @param distance distance to search at.
	 * @return tile in the given direction.
	 */
	public Tile getTileByFacing(Facing facing, int distance) {
		final int x = getEntity().getX() + facing.dx * distance;
		final int y = getEntity().getY() + facing.dy * distance;
		
		return getTileFromVision(x, y);
	}
	
	/**
	 * Searches for the given tile with the given offset to the robots position.
	 * If there is no tile found it will create a temporary tile with the needed coordinates 
	 * and <code>Material.UNDEFINED</code> as material.
	 * 
	 * @param dX delta x.
	 * @param dY delta y.
	 * @return tile at the given offset.
	 */
	public Tile getTileByOffset(int dX, int dY) {
		return getTileFromVision(getEntity().getX() + dX, getEntity().getY() + dY);
	}
	
	/**
	 * Searches for a tile with the given x and y coordinates within the entities vision.
	 * If there is no tile found it will create a temporary tile with the needed coordinates
	 * and <code>Material.UNDEFINED</code> as material.
	 * 
	 * @param x x coordinate of the searched tile.
	 * @param y y coordinate of the searched tile.
	 * @return found tile or temporary tile.
	 * @see AbstractAI#getVision()
	 */
	public Tile getTileFromVision(int x, int y) {
		for (Tile tile : getVision()) {
			if (tile.getX() == x && tile.getY() == y)
				return tile;
		}
		
		return new Tile(null, x, y, Material.UNDEFINED);
	}
	
	/**
	 * Searches for the tile beneath the entity.
	 * If there is no tile found it will create a temporary tile with the needed coordinates 
	 * and <code>Material.UNDEFINED</code> as material.
	 * 
	 * @return tile beneath the entity.
	 */
	public Tile getBeneathTile() {
		final int x = getEntity().getX();
		final int y = getEntity().getY();

		return getTileFromVision(x, y);
	}
	
	/**
	 * Tile to the left of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.UNDEFINED</code> as material.
	 * 
	 * @return tile to the left of the entity.
	 */
	public Tile getLeftTile() {
		return getTileByFacing(getEntity().getFacing().left());
	}
	
	/**
	 * Tile in front of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.UNDEFINED</code> as material.
	 * 
	 * @return tile in front of the entity.
	 */
	public Tile getFrontTile() {
		return getTileByFacing(getEntity().getFacing());
	}
	
	/**
	 * Tile to the right of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.UNDEFINED</code> as material.
	 * 
	 * @return tile to the right of the entity.
	 */
	public Tile getRightTile() {
		return getTileByFacing(getEntity().getFacing().right());
	}
	
	/**
	 * Tile behind of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.UNDEFINED</code> as material.
	 * 
	 * @return tile behind the entity.
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
	 * @return current entity location.
	 */
	public Location getLocation() {
		return new Location(getX(), getY(), getFacing());
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
	
	protected void openMapView() {
		this.mapView = new ClientMapView();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/observerView.fxml"));
		loader.setController(mapView);
		try {
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.NONE);
			stage.initStyle(StageStyle.DECORATED);
			stage.setTitle("Robots -- MapView");			
			stage.setScene(new Scene(root, 800, 600));
			stage.setOnHidden(e -> mapView = null);
			stage.show();
		} catch (IOException e) {
			LogManager.getLogger().catching(e);
			return;
		}
	}
	
	protected void updateMap(Map map) {
		if (mapView != null)
			mapView.updateMap(map);
	}
}
