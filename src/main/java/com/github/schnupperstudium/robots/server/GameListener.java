package com.github.schnupperstudium.robots.server;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.entity.Item;

/**
 * Provides an interfaces to react on things happening in the game and also influence the things happening.
 * 
 * @author Daniel Wieland
 *
 */
public interface GameListener {
	// Game Events
	void onGameStart(Game game);
	void onGameEnd(Game game);
	void onRoundComplete(Game game);
	
	// Entity Events
	boolean canEntitySpawn(Game game, Entity entity);
	void onEntitySpawn(Game game, Entity entity);
	boolean canAISpawn(Game game, AI ai);
	void onAISpawn(Game game, AI ai);
	void onAIDespawn(Game game, AI ai);
	void onEntityDespawn(Game game, Entity entity);
	boolean canEntityMove(Game game, Entity entity, int tX, int tY);
	void onEntityMove(Game game, Entity entity);
	
	// Item Events
	boolean canItemSpawn(Game game, Item item);
	void onItemSpawn(Game game, Item item);
	boolean canPickUpItem(Game game, Item item, InventoryHolder holder);
	void onPickUpItem(Game game, Item item, InventoryHolder holder);
	boolean canDropItem(Game game, Item item, InventoryHolder holder);
	void onDropItem(Game game, Item item, InventoryHolder holder);
	boolean canUseItem(Game game, Item item, InventoryHolder holder);
	void onItemUse(Game game, Item item, InventoryHolder holder);
	
	// Tickables
	void onTickableSpawn(Game game, Tickable tickable);
	
	// Observers
	boolean canObserverJoin(Game game, WorldObserver observer);
	void onObserverJoin(Game game, WorldObserver observer);
	void onObserverQuit(Game game, WorldObserver observer);
}
