package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.server.GameManager;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class EntityMoveEvent extends EntityEvent {
	private final int nextX;
	private final int nextY;
	
	public EntityMoveEvent(World world, Entity entity, Facing facing, int steps) {
		super(world, entity);
		
		nextX = entity.getX() + facing.dx * steps;
		nextY = entity.getY() + facing.dy * steps;
	}
	
	public EntityMoveEvent(World world, Entity entity, int nextX, int nextY) {
		super(world, entity);
		
		this.nextX = nextX;
		this.nextY = nextY;
	}
	
	public int getNextX() {
		return nextX;
	}
	
	public int getNextY() {
		return nextY;
	}

	@Override
	public boolean apply(GameManager manager) {
		Tile currentTile = entity.getTile(world);
		Tile nextTile = world.getTile(nextX, nextY);
		if (!nextTile.canVisit())
			return false;
		
		currentTile.setVisitor(null);
		nextTile.setVisitor(entity);
		return true;
	}
}
