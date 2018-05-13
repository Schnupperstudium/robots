package com.github.schnupperstudium.robots.server;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.UUIDGenerator;
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
import com.github.thedwoon.event.EventPriority;
import com.github.thedwoon.event.SynchronizedEventDispatcher;

public class Game implements Runnable, EventListener {
	private static final long TURN_DURATION = 500;
	
	private final long uuid = UUIDGenerator.obtain();
	private final EventDispatcher eventDispatcher = new SynchronizedEventDispatcher();
	private final List<Tickable> tickables = new ArrayList<>();
	private final String name;
	private final String password;
	private final Level level;
	private final World world;
	
	private boolean running = true;
	
	public Game(String name, Level level) throws FileNotFoundException, URISyntaxException {
		this(name, level, level.loadWorld());
	}
	
	public Game(String name, Level level, String auth) throws FileNotFoundException, URISyntaxException {
		this(name, level, level.loadWorld(), auth);
	}
	
	public Game(String name, Level level, World world) {
		this(name, level, world, null);
	}
	
	public Game(String name, Level level, World world, String password) {
		this.name = name;
		this.level = level;
		this.world = world;
		this.password = password;
		
		eventDispatcher.registerListener(AbstractGameEvent.class, this::executeEvent, EventPriority.MONITOR, true);
	}

	@Override
	public void run() {
		while (running) {
			try {
				final long start = System.currentTimeMillis();
				makeTurn();
				final long end = System.currentTimeMillis();
				final long timeToWait = TURN_DURATION - (end - start);
				if (timeToWait > 0) {
					Thread.sleep(timeToWait);
				} else {
					System.out.printf("Server is behind: %d ms\n", timeToWait);
				}
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}

	protected void makeTurn() {
		List<Tickable> tickables = new ArrayList<>(this.tickables);
		for (Tickable tickable : tickables) {
			tickable.update(this);
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

	public void addTickable(Tickable tickable) {
		tickables.add(tickable);
	}
	
	public void removeTickable(Tickable tickable) {
		tickables.remove(tickable);
	}
	
	public synchronized void restartGame() {
		// TODO: implement
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
	
	public long getUUID() {
		return uuid;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean hasPassword() {
		return password != null;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public String getName() {
		return name;
	}
	
	public GameInfo getGameInfo() {
		return new GameInfo(uuid, name, level, hasPassword());
	}
}
