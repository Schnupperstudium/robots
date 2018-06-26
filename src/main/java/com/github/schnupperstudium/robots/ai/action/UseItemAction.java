package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;

public class UseItemAction extends EntityAction {
	private final long itemUUID;
	
	public UseItemAction(long itemUUID) {
		this.itemUUID = itemUUID;
	}
	
	@Override
	public boolean apply(Game game, Entity entity) {
		if (!entity.hasInventory())
			return false;
		
		Item item = entity.getInventory().findItem(itemUUID);
		if (item == null) {		
			return false;
		} else {
			item.use(game, entity);
			return true;
		}
	}
	
	public long getItemUUID() {
		return itemUUID;
	}
}
