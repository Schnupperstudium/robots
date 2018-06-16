package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.world.World;

public class LivingEntityDespawnEvent extends EntityDespawnEvent {
	public LivingEntityDespawnEvent(World world, LivingEntity entity) {
		super(world, entity);
	}
	
	@Override
	public Entity getEntity() {
		return (LivingEntity) entity;
	}
}
