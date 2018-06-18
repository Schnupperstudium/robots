package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;

public class DropItemAction extends EntityAction {
	private final long uuid;
	
	public DropItemAction(long uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public boolean apply(Game manager, Entity entity) {
		if (entity instanceof InventoryHolder) {
			InventoryHolder holder = (InventoryHolder) entity;
			Inventory inventory = holder.getInventory();
			Item item = inventory.findItem(uuid);
			return manager.dropItem(entity, item);
		}
		
		return false;
	}

	public long getUUID() {
		return uuid;
	}
}
