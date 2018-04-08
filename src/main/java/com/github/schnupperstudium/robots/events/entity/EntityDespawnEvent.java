package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.world.World;

public class EntityDespawnEvent extends EntityEvent {
	public EntityDespawnEvent(World world, Entity entity) {
		super(world, entity);
	}
}
