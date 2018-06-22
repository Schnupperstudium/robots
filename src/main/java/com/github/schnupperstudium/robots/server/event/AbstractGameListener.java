package com.github.schnupperstudium.robots.server.event;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.tickable.AI;
import com.github.schnupperstudium.robots.server.tickable.Tickable;
import com.github.schnupperstudium.robots.server.tickable.WorldObserver;

/**
 * A simple implementation of {@link GameListener} without any effects.
 * This class can be used to overwrite just a single method without having
 * to implement every method.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class AbstractGameListener implements GameListener {
	public AbstractGameListener() {
		
	}
	
	@Override
	public void onGameStart(Game game) { }

	@Override
	public void onGameEnd(Game game) { }

	@Override
	public void onRoundComplete(Game game) { }

	@Override
	public boolean canEntitySpawn(Game game, Entity entity) {
		return true;
	}

	@Override
	public void onEntitySpawn(Game game, Entity entity) { }

	@Override
	public void onEntityDespawn(Game game, Entity entity) { }
	
	@Override
	public boolean canEntityMove(Game game, Entity entity, int tX, int tY) {
		return true;
	}
	
	@Override
	public void onEntityMove(Game game, Entity entity) { }
	
	@Override
	public boolean canAISpawn(Game game, AI ai) {
		return true;
	}
	
	@Override
	public void onAISpawn(Game game, AI ai) { }
	
	@Override
	public void onAIDespawn(Game game, AI ai) { }
	
	@Override
	public boolean canItemSpawn(Game game, Item item) {
		return true;
	}

	@Override
	public void onItemSpawn(Game game, Item item) { }

	@Override
	public boolean canPickUpItem(Game game, Item item, InventoryHolder holder) {
		return true;
	}

	@Override
	public void onPickUpItem(Game game, Item item, InventoryHolder holder) { }

	@Override
	public boolean canDropItem(Game game, Item item, InventoryHolder holder) {
		return true;
	}

	@Override
	public void onDropItem(Game game, Item item, InventoryHolder holder) { }

	@Override
	public boolean canUseItem(Game game, Item item, InventoryHolder holder) {
		return true;
	}
	
	@Override
	public void onItemUse(Game game, Item item, InventoryHolder holder) { }
	
	@Override
	public void onTickableSpawn(Game game, Tickable tickable) { }

	@Override
	public boolean canObserverJoin(Game game, WorldObserver observer) {
		return true;
	}

	@Override
	public void onObserverJoin(Game game, WorldObserver observer) { }

	@Override
	public void onObserverQuit(Game game, WorldObserver observer) { }
}
