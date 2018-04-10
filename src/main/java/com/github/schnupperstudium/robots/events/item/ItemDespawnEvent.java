package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class ItemDespawnEvent extends ItemEvent {

	public ItemDespawnEvent(World world, Item item, int x, int y) {
		super(world, item);
		
		item.setPosition(x, y);
	}
	
	public ItemDespawnEvent(World world, Item item) {
		super(world, item);
	}

	@Override
	protected boolean apply(Game manager) {
		Tile tile = item.getTile(world);
		if (tile.getItem() != item)
			return false;
		
		tile.setItem(null);
		return true;
	}

}
