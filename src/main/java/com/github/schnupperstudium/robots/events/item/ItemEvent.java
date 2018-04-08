package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.events.AbstractEvent;
import com.github.schnupperstudium.robots.world.World;

public abstract class ItemEvent extends AbstractEvent {
	private final World world;
	private final Item item;
	
	public ItemEvent(World world, Item item) {
		this.world = world;
		this.item = item;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Item getItem() {
		return item;
	}
}
