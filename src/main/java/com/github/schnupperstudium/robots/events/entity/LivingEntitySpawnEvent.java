package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.world.World;

public class LivingEntitySpawnEvent extends EntitySpawnEvent {
	
	public LivingEntitySpawnEvent(World world, LivingEntity entity) {
		super(world, entity);
	}
	
	@Override
	public LivingEntity getEntity() {
		return (LivingEntity) entity;
	}
}
