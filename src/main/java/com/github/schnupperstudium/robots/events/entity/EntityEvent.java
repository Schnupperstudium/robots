package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.events.AbstractGameEvent;
import com.github.schnupperstudium.robots.world.World;

public abstract class EntityEvent extends AbstractGameEvent {
	protected final Entity entity;
	protected final World world;
	
	public EntityEvent(World world, Entity entity) {
		this.entity = entity;
		this.world = world;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public World getWorld() {
		return world;
	}
}
