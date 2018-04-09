package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.events.AbstractGameEvent;
import com.github.schnupperstudium.robots.world.World;

public abstract class ItemEvent extends AbstractGameEvent {
	protected final World world;
	protected final Item item;
	
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
