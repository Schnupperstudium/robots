package com.github.schnupperstudium.robots.server;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.entity.Item;

public class MasterGameListener extends MasterListener<GameListener> implements GameListener {
	public MasterGameListener() {
		
	}

	@Override
	public void onGameStart(Game game) {
		notifyListeners(l -> l.onGameStart(game));
	}

	@Override
	public void onGameEnd(Game game) {
		notifyListeners(l -> l.onGameEnd(game));
	}

	@Override
	public void onRoundComplete(Game game) {
		notifyListeners(l -> l.onRoundComplete(game));
	}

	@Override
	public boolean canEntitySpawn(Game game, Entity entity) {
		return consultListeners(l -> l.canEntitySpawn(game, entity));
	}

	@Override
	public void onEntitySpawn(Game game, Entity entity) {
		notifyListeners(l -> l.onEntitySpawn(game, entity));
	}

	@Override
	public void onEntityDespawn(Game game, Entity entity) {
		notifyListeners(l -> l.onEntityDespawn(game, entity));
	}
	
	@Override
	public boolean canEntityMove(Game game, Entity entity, int tX, int tY) {
		return consultListeners(l -> l.canEntityMove(game, entity, tX, tY));
	}

	@Override
	public void onEntityMove(Game game, Entity entity) {
		notifyListeners(l -> l.onEntityMove(game, entity));
	}

	@Override
	public boolean canItemSpawn(Game game, Item item) {
		return consultListeners(l -> l.canItemSpawn(game, item));
	}

	@Override
	public void onItemSpawn(Game game, Item item) {
		notifyListeners(l -> l.onItemSpawn(game, item));
	}

	@Override
	public boolean canPickUpItem(Game game, Item item, InventoryHolder holder) {
		return consultListeners(l -> l.canPickUpItem(game, item, holder));
	}

	@Override
	public void onPickUpItem(Game game, Item item, InventoryHolder holder) {
		notifyListeners(l -> l.onPickUpItem(game, item, holder));
	}

	@Override
	public boolean canDropItem(Game game, Item item, InventoryHolder holder) {
		return consultListeners(l -> l.canDropItem(game, item, holder));
	}

	@Override
	public void onDropItem(Game game, Item item, InventoryHolder holder) {
		notifyListeners(l -> l.onDropItem(game, item, holder));
	}

	@Override
	public boolean canUseItem(Game game, Item item, InventoryHolder holder) {
		return consultListeners(l -> l.canUseItem(game, item, holder));
	}
	
	@Override
	public void onItemUse(Game game, Item item, InventoryHolder holder) {
		notifyListeners(l -> l.onItemUse(game, item, holder));
	}
	
	@Override
	public void onTickableSpawn(Game game, Tickable tickable) {
		notifyListeners(l -> l.onTickableSpawn(game, tickable));
	}

	@Override
	public boolean canObserverJoin(Game game, WorldObserver observer) {
		return consultListeners(l -> l.canObserverJoin(game, observer));
	}

	@Override
	public void onObserverJoin(Game game, WorldObserver observer) {
		notifyListeners(l -> l.onObserverJoin(game, observer));
	}

	@Override
	public void onObserverQuit(Game game, WorldObserver observer) {
		notifyListeners(l -> l.onObserverQuit(game, observer));
	}

	@Override
	public boolean canAISpawn(Game game, AI ai) {
		return consultListeners(l -> l.canAISpawn(game, ai));
	}

	@Override
	public void onAISpawn(Game game, AI ai) {
		notifyListeners(l -> l.onAISpawn(game, ai));
	}
	
	@Override
	public void onAIDespawn(Game game, AI ai) {
		notifyListeners(l -> l.onAIDespawn(game, ai));
	}
}
