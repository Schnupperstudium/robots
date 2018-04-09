package com.github.schnupperstudium.robots.events.item;

import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.GameManager;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class ItemSpawnEvent extends ItemEvent {

	public ItemSpawnEvent(World world, Item item, int x, int y) {
		super(world, item);
		
		item.setPosition(x, y);
	}
	
	public ItemSpawnEvent(World world, Item item) {
		super(world, item);
	}

	@Override
	protected boolean apply(GameManager manager) {
		Tile tile = item.getTile(world);
		if (tile.getItem() != null)
			return false;
		
		tile.setItem(item);
		return true;
	}

}
