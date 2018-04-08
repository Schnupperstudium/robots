package com.github.schnupperstudium.robots.server;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.events.entity.EntityDespawnEvent;
import com.github.schnupperstudium.robots.events.entity.EntityMoveEvent;
import com.github.schnupperstudium.robots.events.entity.EntitySpawnEvent;
import com.github.schnupperstudium.robots.events.item.ItemDropEvent;
import com.github.schnupperstudium.robots.events.item.ItemPickUpEvent;
import com.github.schnupperstudium.robots.events.item.UseItemEvent;
import com.github.schnupperstudium.robots.world.Field;
import com.github.schnupperstudium.robots.world.World;
import com.github.thedwoon.event.EventDispatcher;
import com.github.thedwoon.event.SynchronizedEventDispatcher;

public class GameManager implements Runnable {
	private static final long TURN_DURATION = 500;
	
	private final EventDispatcher eventDispatcher = new SynchronizedEventDispatcher();
	private final List<Module> modules = new ArrayList<>();
	private final List<Entity> entities = new ArrayList<>();
	private final World world;
	
	private boolean running = true;
	
	public GameManager(World world) {
		this.world = world;
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
	
	public synchronized boolean spawnEntity(Entity entity, int x, int y) {
		entity.setPosition(x, y);
		return spawnEntity(entity);
	}
	
	public synchronized boolean spawnEntity(Entity entity) {
		Field field = entity.getField(world);
		if (!field.canVisit())
			return false;
		
		EntitySpawnEvent event = new EntitySpawnEvent(world, entity);
		eventDispatcher.dispatchEvent(event);
		if (event.isCanceled())
			return false;
		
		entities.add(entity);
		field.setVisitor(entity);
		return true;
	}
	
	public synchronized boolean despawnEntity(Entity entity) {
		if (!entities.contains(entity))
			return false;
		
		Field field = entity.getField(world);
		if (field.getVisitor() != entity)
			return false;
		
		EntityDespawnEvent event = new EntityDespawnEvent(world, entity);
		eventDispatcher.dispatchEvent(event);
		if (event.isCanceled())
			return false;
		
		entities.remove(entity);
		field.setVisitor(null);
		return true;
	}
	
	public synchronized boolean moveEntity(Entity entity, Facing facing) {
		return moveEntity(entity, facing, 1);
	}
	
	public synchronized boolean moveEntity(Entity entity, Facing facing, int steps) {
		final Field currentField = entity.getField(world);
		if (currentField.getVisitor() != entity)
			return false;
		
		final int nextX = entity.getX() + facing.dx * steps;
		final int nextY = entity.getY() + facing.dy * steps;
		Field nextField = world.getField(nextX, nextY);
		if (!nextField.canVisit())
			return false;
		
		EntityMoveEvent event = new EntityMoveEvent(world, entity, nextX, nextY);
		eventDispatcher.dispatchEvent(event);
		if (event.isCanceled())
			return false;
		
		currentField.setVisitor(null);
		entity.setPosition(nextX, nextY);
		nextField.setVisitor(entity);		
		return true;
	}
	
	public synchronized boolean dropItem(Entity entity, Item item) {
		Field field = entity.getField(world);
		if (field.getItem() != null)
			return false;
		
		ItemDropEvent event = new ItemDropEvent(world, entity, item);
		eventDispatcher.dispatchEvent(event);
		if (event.isCanceled())
			return false;
		
		field.setItem(item);
		return true;
	}
	
	public synchronized Item pickUpItem(Entity entity) {
		Field field = entity.getField(world);
		if (field.getItem() != null) {
			ItemPickUpEvent event = new ItemPickUpEvent(world, entity, field.getItem());
			eventDispatcher.dispatchEvent(event);
			if (event.isCanceled())
				return null;
		}
		
		return field.getItem();
	}
	
	public synchronized boolean useItem(Entity user, Item item) {
		UseItemEvent event = new UseItemEvent(world, user, item);
		eventDispatcher.dispatchEvent(event);
		if (event.isCanceled())
			return false;
		
		item.use(this, user);
		return true;
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
