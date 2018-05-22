package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;

public class PickUpItemAction extends EntityAction {
	public static final PickUpItemAction INSTANCE = new PickUpItemAction();
	
	public PickUpItemAction() {
		
	}
	
	@Override
	public boolean apply(Game manager, Entity entity) {		
		if (entity instanceof InventoryHolder) {
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			Item item = manager.pickUpItem(entity);
			if (item != null) {
				inventory.addItem(item);
				return true;
			}
		}
		
		return false;
	}
}
