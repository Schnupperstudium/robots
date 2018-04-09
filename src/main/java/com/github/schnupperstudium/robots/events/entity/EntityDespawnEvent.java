package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.GameManager;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class EntityDespawnEvent extends EntityEvent {
	public EntityDespawnEvent(World world, Entity entity) {
		super(world, entity);
	}

	@Override
	public boolean apply(GameManager manager) {
		Tile tile = entity.getTile(world);
		if (tile.getVisitor() != entity)
			return false;
		
		tile.setVisitor(null);
		return true;
	}
}
