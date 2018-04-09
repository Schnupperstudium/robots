package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.GameManager;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class ItemDropEvent extends ItemEvent {
	private final Entity entity;
	
	public ItemDropEvent(World world, Entity entity, Item item) {
		super(world, item);
		
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public boolean apply(GameManager manager) {
		Tile tile = entity.getTile(world);
		if (tile.getItem() != null)
			return false;
		
		// if the entity holds a inventory try to remove the item.
		// but don't deny it for a non inventory holder to drop items.
		if (entity instanceof InventoryHolder) {
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			if (!inventory.removeItem(item))
				return false;
		}
		
		tile.setItem(item);
		return true;
	}
}
