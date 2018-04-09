package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.GameManager;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class EntitySpawnEvent extends EntityEvent {
	
	public EntitySpawnEvent(World world, Entity entity, int x, int y) {
		super(world, entity);
		
		entity.setPosition(x, y);
	}
	
	public EntitySpawnEvent(World world, Entity entity) {
		super(world, entity);
	}

	@Override
	public boolean apply(GameManager manager) {
		Tile tile = entity.getTile(world);
		if (!tile.canVisit())
			return false;
		
		tile.setVisitor(entity);
		return true;
	}
}
