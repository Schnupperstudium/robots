package com.github.schnupperstudium.robots.server;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.events.AbstractGameEvent;
import com.github.schnupperstudium.robots.events.entity.EntityDespawnEvent;
import com.github.schnupperstudium.robots.events.entity.EntityMoveEvent;
import com.github.schnupperstudium.robots.events.entity.EntitySpawnEvent;
import com.github.schnupperstudium.robots.events.item.ItemDropEvent;
import com.github.schnupperstudium.robots.events.item.ItemPickUpEvent;
import com.github.schnupperstudium.robots.events.item.UseItemEvent;
import com.github.schnupperstudium.robots.world.World;
import com.github.thedwoon.event.EventDispatcher;
import com.github.thedwoon.event.EventListener;
import com.github.thedwoon.event.SynchronizedEventDispatcher;

public class GameManager implements Runnable, EventListener {
	private static final long TURN_DURATION = 500;
	
	private final EventDispatcher eventDispatcher = new SynchronizedEventDispatcher();
	private final List<Module> modules = new ArrayList<>();
	private final World world;
	
	private boolean running = true;
	
	public GameManager(World world) {
		this.world = world;
		
		eventDispatcher.registerListener(AbstractGameEvent.class, this::executeEvent);
	}

	@Override
	public void run() {
		while (running) {
			try {
				makeTurn();
				modules.forEach(module -> module.updateModule());
			} catch (InterruptedException e) {
				running = false;
			}
		}
		
		modules.forEach(module -> module.unload());
		modules.clear();
	}

	protected void makeTurn() throws InterruptedException {
		final long start = System.currentTimeMillis();
		modules.forEach(module -> module.updateModule());
		final long end = System.currentTimeMillis();
		final long timeToWait = TURN_DURATION - (end - start);
		if (timeToWait > 0) {
			Thread.sleep(timeToWait);
		} else {
			System.out.printf("Server is behind: %d ms\n", timeToWait);
		}
	}
	
	private void executeEvent(AbstractGameEvent event) {
		event.executeEvent(this);
	}
	
	public synchronized boolean spawnEntity(Entity entity, int x, int y) {
		entity.setPosition(x, y);
		return spawnEntity(entity);
	}
	
	public synchronized boolean spawnEntity(Entity entity) {
		EntitySpawnEvent event = new EntitySpawnEvent(world, entity);
		eventDispatcher.dispatchEvent(event);
		return event.isSuccessful();
	}
	
	public synchronized boolean despawnEntity(Entity entity) {		
		EntityDespawnEvent event = new EntityDespawnEvent(world, entity);
		eventDispatcher.dispatchEvent(event);		
		return event.isSuccessful();
	}
	
	public synchronized boolean moveEntity(Entity entity, Facing facing) {
		return moveEntity(entity, facing, 1);
	}
	
	public synchronized boolean moveEntity(Entity entity, Facing facing, int steps) {
		EntityMoveEvent event = new EntityMoveEvent(world, entity, facing, steps);
		eventDispatcher.dispatchEvent(event);
		return event.isSuccessful();
	}
	
	public synchronized boolean dropItem(Entity entity, Item item) {		
		ItemDropEvent event = new ItemDropEvent(world, entity, item);
		eventDispatcher.dispatchEvent(event);
		return event.isSuccessful();
	}
	
	public synchronized Item pickUpItem(Entity entity) {
		ItemPickUpEvent event = new ItemPickUpEvent(world, entity);
		eventDispatcher.dispatchEvent(event);
		if (event.isSuccessful())
			return event.getItem();
		else
			return null;
	}
	
	public synchronized void useItem(Entity user, Item item) {
		UseItemEvent event = new UseItemEvent(world, user, item);
		eventDispatcher.dispatchEvent(event);		
	}
	
	public synchronized void addModule(Module module) {
		module.load(this);
	}
	
	public synchronized void restartGame() {
		
	}
	
	public synchronized void endGame() {
		running = false;
	}
	
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}
	
	public World getWorld() {
		return world;
	}
}
