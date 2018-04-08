package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.world.World;

public class ItemPickUpEvent extends ItemEvent {
	private final Entity entity;
	
	public ItemPickUpEvent(World world, Entity entity, Item item) {
		super(world, item);
		
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
