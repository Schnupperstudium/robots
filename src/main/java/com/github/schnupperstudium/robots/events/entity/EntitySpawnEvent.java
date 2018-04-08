package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.world.World;

public class EntitySpawnEvent extends EntityEvent {
	public EntitySpawnEvent(World world, Entity entity) {
		super(world, entity);
	}
}
