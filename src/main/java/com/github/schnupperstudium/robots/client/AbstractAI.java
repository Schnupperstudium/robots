package com.github.schnupperstudium.robots.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.world.Tile;

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
	
	public abstract EntityAction makeTurn();
	
	public long getEntityUUID() {
		return entityUUID;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public List<Tile> getVision() {
		return vision;
	}
}
