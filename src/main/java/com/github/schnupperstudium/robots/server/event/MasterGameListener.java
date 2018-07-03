package com.github.schnupperstudium.robots.server.event;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.tickable.AI;
import com.github.schnupperstudium.robots.server.tickable.Tickable;
import com.github.schnupperstudium.robots.server.tickable.WorldObserver;

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
	public void onEntityMove(Game game, Entity entity, int sX, int sY) {
		notifyListeners(l -> l.onEntityMove(game, entity, sX, sY));
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
	public boolean canPickUpItem(Game game, Entity entity, Item item) {
		return consultListeners(l -> l.canPickUpItem(game, entity, item));
	}

	@Override
	public void onPickUpItem(Game game, Entity entity, Item item) {
		notifyListeners(l -> l.onPickUpItem(game, entity, item));
	}

	@Override
	public boolean canDropItem(Game game, Entity entity, Item item) {
		return consultListeners(l -> l.canDropItem(game, entity, item));
	}

	@Override
	public void onDropItem(Game game, Entity entity, Item item) {
		notifyListeners(l -> l.onDropItem(game, entity, item));
	}

	@Override
	public boolean canUseItem(Game game, Entity entity, Item item) {
		return consultListeners(l -> l.canUseItem(game, entity, item));
	}
	
	@Override
	public void onItemUse(Game game, Entity entity, Item item) {
		notifyListeners(l -> l.onItemUse(game, entity, item));
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
