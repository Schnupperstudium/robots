package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class ItemPickUpEvent extends ItemEvent {
	private final Entity entity;
	
	public ItemPickUpEvent(World world, Entity entity) {
		super(world, entity.getTile(world).getItem());
		
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public boolean apply(Game manager) {
		if (item == null)
			return false;
		
		if (entity instanceof InventoryHolder) {
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			if (!inventory.addItem(item)) 
				return false;
		}
		
		Tile tile = entity.getTile(world);		
		tile.setItem(null);
		return true;
	}
}
