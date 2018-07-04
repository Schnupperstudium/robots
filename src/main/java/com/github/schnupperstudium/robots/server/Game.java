package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.event.MasterGameListener;
import com.github.schnupperstudium.robots.server.module.GameModule;
import com.github.schnupperstudium.robots.server.tickable.Tickable;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class Game implements Runnable {
	private static final Logger LOG = LogManager.getLogger();
	private static final long TURN_DURATION = 200;
	private static final int MAX_IDLE_TIME = 120000;
	
	private final long uuid = UUIDGenerator.obtain();
	private final MasterGameListener masterGameListener = new MasterGameListener();
	private final List<Tickable> tickables = new ArrayList<>();
	private final List<GameModule> modules;
	private final RobotsServer server;
	private final Thread thread;
	private final String name;
	private final String password;
	private final Level level;
	private final World world;
	
	private int idleTime = 0;
	private boolean running = true;
	
	public Game(RobotsServer server, String name, Level level) throws IOException {
		this(server, name, level, level.loadWorld());
	}
	
	public Game(RobotsServer server, String name, Level level, String auth) throws IOException {
		this(server, name, level, level.loadWorld(), auth);
	}
	
	public Game(RobotsServer server, String name, Level level, World world) {
		this(server, name, level, world, null);
	}
	
	public Game(RobotsServer server, String name, Level level, World world, String password) {
		this.server = server;
		this.name = name;
		this.level = level;
		this.world = world;
		this.password = password;
		this.modules = level.loadModules();
		this.thread = new Thread(this::run, "GameThread: (" + name + ":" + uuid + ")");
		this.thread.start();		
	}

	@Override
	public void run() {
		for (GameModule module : modules) {
			module.init(server, this);
		}
		
		masterGameListener.onGameStart(this);
		
		while (running) {
			try {
				final long start = System.currentTimeMillis();
				makeTurn();
				final long end = System.currentTimeMillis();
				final long timeToWait = TURN_DURATION - (end - start);
				if (timeToWait > 0) {
					Thread.sleep(timeToWait);
				} else {
					LOG.warn("Server is behind: {} ms", timeToWait);
				}
			} catch (InterruptedException e) {
				running = false;
			}
		}
		
		masterGameListener.onGameEnd(this);
		
		LOG.info("{}:{} has stopped (Reason: {})", getName(), getUUID(), (idleTime >= MAX_IDLE_TIME ? "IDLE" : "FINISHED"));
	}

	protected void makeTurn() {
		if (tickables.isEmpty())
			idleTime += TURN_DURATION;
		else
			idleTime = 0;
				
		List<Tickable> tickables = new ArrayList<>(this.tickables);
		for (Tickable tickable : tickables) {
			tickable.update(this);
		}
		
		masterGameListener.onRoundComplete(this);
		
		if (idleTime >= MAX_IDLE_TIME) {			
			endGame();
		}
	}
	
	public synchronized boolean spawnEntity(Entity entity, int x, int y) {
		if (entity == null)
			return false;
		
		entity.setPosition(x, y);
		return spawnEntity(entity);
	}
	
	public synchronized boolean spawnEntity(Entity entity) {
		if (entity == null || !masterGameListener.canEntitySpawn(this, entity))
			return false;
		
		Tile tile = entity.getTile(world);
		if (!tile.canVisit())
			return false;
		
		entity.setWorld(world);
		tile.setVisitor(entity);
		masterGameListener.onEntitySpawn(this, entity);
		return true;
	}
	
	public synchronized boolean despawnEntity(Entity entity) {
		if (entity == null)
			return false;
		
		Tile tile = entity.getTile(world);
		entity.setWorld(null);
		tile.clearVisitor(entity);
		masterGameListener.onEntityDespawn(this, entity);
		return true;
	}
	
	public synchronized boolean moveEntity(Entity entity, Facing facing) {
		return moveEntity(entity, facing, 1);
	}
	
	public synchronized boolean moveEntity(Entity entity, Facing facing, int steps) {
		final int sX = entity.getX();
		final int sY = entity.getY();
		final int tX = entity.getX() + facing.dx * steps;
		final int tY = entity.getY() + facing.dy * steps;
		
		if (!masterGameListener.canEntityMove(this, entity, tX, tY))
			return false;
		
		Tile currentTile = entity.getTile(world);
		Tile nextTile = world.getTile(tX, tY);
		if (!nextTile.canVisit())
			return false;
		
		currentTile.setVisitor(null);
		nextTile.setVisitor(entity);
		masterGameListener.onEntityMove(this, entity, sX, sY);
		return true;
	}
	
	public synchronized boolean dropItem(Entity entity, Item item) {
		if (!masterGameListener.canDropItem(this, entity, item))
			return false;
		
		Tile tile = entity.getTile(world);
		if (tile.hasItem() || item == null)
			return false;
		
		// if the entity holds a inventory try to remove the item.
		// but don't deny it for a non inventory holder to drop items.
		if (entity != null) {
			Inventory inventory = entity.getInventory();
			if (!inventory.removeItem(item))
				return false;
		}
		
		tile.setItem(item);
		item.setWorld(world);
		masterGameListener.onDropItem(this, entity, item);
		return true;
	}
	
	public synchronized Item pickUpItem(Entity entity) {
		Tile tile = entity.getTile(world);
		Item item = tile.getItem();
		if (item == null || !masterGameListener.canPickUpItem(this, entity, item))
			return null;
		
		Inventory inventory = entity.getInventory();
		if (!inventory.addItem(item)) 
			return null;
		
		item.setWorld(null);
		tile.setItem(null);
		masterGameListener.onPickUpItem(this, entity, item);
		return item;
	}
	
	public synchronized void useItem(Entity entity, Item item) {
		if (entity == null || item == null || !masterGameListener.canUseItem(this, entity, item))
			return;
		
		masterGameListener.onItemUse(this, entity, item);
		item.use(this, entity); 
	}
	
	public List<Tickable> getTickales(Predicate<Tickable> filter) {
		List<Tickable> result = new LinkedList<>(tickables);
		Iterator<Tickable> it = result.iterator();
		while (it.hasNext())
			if (!filter.test(it.next()))
				it.remove();
				
		return result;
	}
	
	public List<Tickable> getTickables() {
		return new ArrayList<>(tickables);
	}
	
	public void addTickable(Tickable tickable) {
		tickables.add(tickable);
	}
	
	public void removeTickable(Tickable tickable) {
		tickables.remove(tickable);
	}
	
	public void addModule(GameModule module) {
		if (module == null) 
			return;
		
		modules.add(module);
		module.init(server, this);
	}
	
	public <T extends GameModule> T getModule(Class<T> moduleClazz) {
		for (GameModule module : modules) {
			if (module.getClass() == moduleClazz)
				return moduleClazz.cast(module);
		}
		
		return null;
	}
	
	public synchronized void restartGame() {
		// TODO: implement
	}
	
	public synchronized void endGame() {
		running = false;
	}
		
	public RobotsServer getServer() {
		return server;
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
		return password != null && !password.isEmpty();
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
	
	public MasterGameListener getMasterGameListener() {
		return masterGameListener;
	}
}
