package com.github.schnupperstudium.robots.server.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class RespawnItemsModule extends AbstractGameListener implements GameModule {
	private static final Logger LOG = LogManager.getLogger();
	
	private final List<Item> spawnItems = new ArrayList<>();
	
	public RespawnItemsModule() {

	}
	
	@Override
	public void init(RobotsServer server, Game game) {
		game.getMasterGameListener().registerListener(this);
		
		final World world = game.getWorld();
		final int width = world.getWidth();
		final int height = world.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile tile = world.getTile(x, y);
				if (!tile.hasItem())
					continue;
				
				Item item = tile.getItem();
				try {
					spawnItems.add(item.clone());
				} catch (CloneNotSupportedException e) {
					LOG.catching(e);
				}
			}
		}
	}

	@Override
	public void onEntityDespawn(Game game, Entity entity) {
		if (!entity.hasInventory())
			return;
		
		Inventory inventory = entity.getInventory();
		for (Item item : inventory.getItems()) {
			Item reference = null;
			for (Item referenceItem : spawnItems) {
				if (referenceItem.getUUID() == item.getUUID()) {
					reference = referenceItem;
					break;
				}
			}
			
			if (reference != null) {				
				Tile tile = reference.getTile(game.getWorld());
				if (tile.hasItem()) {
					LOG.warn("failed to respawn item {}. Spot already used", item.toString());
					continue;
				}
				
				tile.setItem(item);
				game.getMasterGameListener().onItemSpawn(game, item);
			}
		}
	}
	
	@Override
	public void onGameEnd(Game game) {		
		game.getMasterGameListener().removeListener(this);
	}
}
