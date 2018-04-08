package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.world.World;

public class EntityMoveEvent extends EntityEvent {
	private final int nextX;
	private final int nextY;
	
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
}
