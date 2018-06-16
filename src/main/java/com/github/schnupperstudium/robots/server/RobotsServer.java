package com.github.schnupperstudium.robots.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.events.AbstractServerEvent;
import com.github.schnupperstudium.robots.events.entity.AIDespawnEvent;
import com.github.schnupperstudium.robots.events.entity.AISpawnEvent;
import com.github.schnupperstudium.robots.events.game.ObserverJoinEvent;
import com.github.schnupperstudium.robots.events.game.ObserverLeftEvent;
import com.github.schnupperstudium.robots.events.server.GameStartEvent;
import com.github.schnupperstudium.robots.io.LevelParser;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.thedwoon.event.EventDispatcher;
import com.github.thedwoon.event.EventPriority;
import com.github.thedwoon.event.SynchronizedEventDispatcher;

public abstract class RobotsServer implements Runnable {
	public static final long ERR_INVALID_ENTITY_NAME = -1;
	public static final long ERR_GAME_NOT_FOUND = -2;
	public static final long ERR_INVALID_PASSWORD = -3;
	public static final long ERR_NO_SPAWN_FOUND = -4;
	public static final long ERR_SPAWN_DENIED = -5;
	public static final long ERR_INVALID_ENTITY_TYPE = -6;	
	public static final long ERR_INVALID_LEVEL_NAME = -7;
	public static final long ERR_LEVEL_NOT_FOUND = -8;
	public static final long ERR_FAILED_GAME_START = -9;
	public static final long ERR_GAME_START_DENIED = -10;
	
	private static final Logger LOG = LogManager.getLogger();
	
	protected final Map<Class<? extends LivingEntity>, LivingEntityFactory> entityFactories = new HashMap<>();
	protected final EventDispatcher eventDispatcher = new SynchronizedEventDispatcher();
	protected final List<Game> games = new ArrayList<>();
	protected final List<Level> availableLevels = new ArrayList<>();

	private boolean run = true;
	
	public RobotsServer() throws IOException {
		eventDispatcher.registerListener(AbstractServerEvent.class, this::executeEvent, EventPriority.MONITOR, true);
		
		loadLevels();
		loadEntityFactories();
	}
		
	private void executeEvent(AbstractServerEvent event) {
		event.executeEvent(this);
	}

	private void loadEntityFactories() {
		// factory for every playable class
		entityFactories.put(Robot.class, (game, x, y, name) -> { Robot robot = new Robot(name); robot.setPosition(x, y); return robot; });
	}
	
	private void loadLevels() throws IOException {
		availableLevels.clear();
		
		URL url = RobotsServer.class.getResource("/level/");
		if (url == null) {
			LOG.error("Failed to load levels");
			return;
		}
		
		File levelDir = null;
		try {
			levelDir = new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		File[] levels = levelDir.listFiles((dir, name) -> name.endsWith(".level"));
		for (File level : levels) {
			Level l = LevelParser.loadLevel(level);
			if (l != null)
				availableLevels.add(l);
		}
		
		LOG.info("Loaded " + availableLevels.size() + " levels");
	}
	
	@Override
	public void run() {
		while (run) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public Level findLevel(String levelName) {
		for (Level level : availableLevels) {
			if (level.getName().equals(levelName))
				return level;
		}
		
		return null;
	}
	
	public Game findGame(long uuid) {
		synchronized (games) {
			for (Game game : games) {
				if (game.getUUID() == uuid)
					return game;
			}			
		}
		
		return null;
	}
			
	public long startGame(String name, String levelName, String auth) {
		if (name == null || name.isEmpty())
			return ERR_INVALID_ENTITY_NAME;
		else if (levelName == null || levelName.isEmpty())
			return ERR_INVALID_LEVEL_NAME;
		
		Level level = findLevel(levelName);
		if (level == null)
			return ERR_LEVEL_NOT_FOUND;
		
		Game game = null;
		try {
			game = new Game(this, name, level, auth);
		} catch (IOException e) {
			LOG.warn("failed to start game '{}', '{}': {}", name, level, e.getMessage());
			return ERR_FAILED_GAME_START;
		}
		
		GameStartEvent event = new GameStartEvent(game);
		eventDispatcher.dispatchEvent(event);
		if (!event.isSuccessful()) {
			return ERR_GAME_START_DENIED;
		}
		
		LOG.info("started game [id: {}, name: {}, level: {}, map: {}]", 
				game.getUUID(), name, levelName, game.getLevel().getMapLocation());
		return game.getUUID();
	}
	
	public long spawnAI(long gameId, String name, String auth, RobotsClientInterface clientInterface) {
		return spawnAI(gameId, name, auth, clientInterface, Robot.class);
	}
	
	public long spawnAI(long gameId, String name, String auth, RobotsClientInterface clientInterface, String entityType) {
		try {
			if (entityType == null || entityType.isEmpty())
				return ERR_INVALID_ENTITY_TYPE;
			
			Class<?> clazz = Class.forName(entityType);
			if (!LivingEntity.class.isAssignableFrom(clazz)) {
				return ERR_INVALID_ENTITY_TYPE;
			}
			
			Class<? extends LivingEntity> entityClass = clazz.asSubclass(LivingEntity.class);
			return spawnAI(gameId, name, auth, clientInterface, entityClass);
		} catch (ClassNotFoundException e) {
			LOG.warn("failed to find entityClass '{}' while attempting to spawn '{}' in game {}", entityType, name, gameId);
			return ERR_INVALID_ENTITY_TYPE;
		}
	}
	
	public long spawnAI(long gameId, String name, String auth, RobotsClientInterface clientInterface, Class<? extends LivingEntity> entityClass) {
		if (name == null || name.isEmpty())
			return ERR_INVALID_ENTITY_NAME;
		
		if (entityClass == null)
			return ERR_INVALID_ENTITY_TYPE;
		
		Game game = findGame(gameId);
		if (game == null)
			return ERR_GAME_NOT_FOUND;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return ERR_INVALID_PASSWORD;
		
		List<Tile> spawnTiles = game.getWorld().getSpawns();
		Tile spawnTile = null;
		while (!spawnTiles.isEmpty() && spawnTile == null) {
			int index = (int) (Math.random() * spawnTiles.size());
			spawnTile = spawnTiles.remove(index);
			if (!spawnTile.canVisit())
				spawnTile = null;
		}
		if (spawnTile == null)
			return ERR_NO_SPAWN_FOUND;
		
		LivingEntityFactory factory = entityFactories.get(entityClass);
		if (factory == null)
			return ERR_INVALID_ENTITY_TYPE;
		LivingEntity entity = factory.create(game, spawnTile.getX(), spawnTile.getY(), name);
		AISpawnEvent event = new AISpawnEvent(game, game.getWorld(), entity, clientInterface);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful()) {
			LOG.info("spawned AI '{}':{} in game '{}':{}", name, entity.getUUID(), game.getName(), gameId);
			return entity.getUUID();
		} else {
			LOG.warn("failed to spawn AI '{}':{} in game '{}':{} using auth '{}'", name, entity.getUUID(), game.getName(), gameId, auth);
			return ERR_SPAWN_DENIED;
		}
	}
	
	public boolean despawnAI(long gameId, long entityUUID) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		List<Tickable> possibleAIs = game.getTickales(t -> t instanceof AI && ((AI) t).getEntity().getUUID() == entityUUID);
		if (possibleAIs.isEmpty())
			return false;
		
		AI ai = (AI) possibleAIs.get(0);
		AIDespawnEvent event = new AIDespawnEvent(game.getWorld(), ai.getEntity(), ai);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful())
			LOG.info("{}:{} was removed from game '{}':{}", ai.getEntity().getName(), ai.getEntity().getUUID(), game.getName(), game.getUUID());
		else
			LOG.warn("{} could not be removed from game '{}':{}", entityUUID, game.getName(), game.getUUID());
		
		return event.isSuccessful();
	}
	
	public boolean observeWorld(long gameId, String auth, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return false;
		
		ObserverJoinEvent event = new ObserverJoinEvent(clientInterface);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful())
			LOG.info("added observer for game '{}':{}", game.getName(), gameId);
		else
			LOG.warn("failed to add observer for game '{}':{} with auth '{}'", game.getName(), gameId, auth);
		
		return event.isSuccessful();
	}
	
	public boolean unobserveWorld(long gameId, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		List<Tickable> possibleObservers = game.getTickales(t -> (t instanceof WorldObserver) && ((WorldObserver) t).getClientInterface() == clientInterface);
		if (possibleObservers.isEmpty())
			return false;
		
		WorldObserver observer = (WorldObserver) possibleObservers.get(0);
		ObserverLeftEvent event = new ObserverLeftEvent(observer);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful())
			LOG.info("removed observer from game '{}':{}", game.getName(), gameId);
		else
			LOG.warn("failed to locate observer in game '{}':{}", game.getName(), gameId);
		
		return event.isSuccessful();
	}
	
	public void addGame(Game game) {
		if (game == null)
			return;
		
		synchronized (games) {
			games.add(game);
		}
	}
	
	public void removeGame(Game game) {
		if (game == null)
			return;
		
		synchronized (games) {
			games.remove(game);
		}
	}
	
	public List<Level> listLevels() {
		return availableLevels;
	}
	
	public List<GameInfo> listGames() {
		List<GameInfo> gameInfos = new LinkedList<>();
		synchronized (games) {
			games.stream().map(g -> g.getGameInfo()).forEach(gameInfos::add);
		}

		return gameInfos;
	}
	
	public void close() throws IOException {
		run = false;
		synchronized (games) {
			games.forEach(game -> game.endGame());
		}
		
		LOG.info("server closed");
	}
	
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}
}
