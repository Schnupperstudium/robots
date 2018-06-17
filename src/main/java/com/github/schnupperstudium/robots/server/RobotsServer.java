package com.github.schnupperstudium.robots.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	public static final long ERR_ENTITY_LIMIT_EXCEEDED = -11;
	
	private static final Logger LOG = LogManager.getLogger();
	
	protected final Map<Long, ClientTracker> clientTrackers = new HashMap<>();
	protected final Map<String, LivingEntityFactory> entityFactories = new HashMap<>();
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
		entityFactories.put(Robot.class.getName(), (game, x, y, name) -> { Robot robot = new Robot(name); robot.setPosition(x, y); return robot; });
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
			
	public long startGame(long connectionId, String name, String levelName, String auth, RobotsClientInterface clientInterface) {
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
	
	public long spawnAI(long connectionId, long gameId, String name, String auth, RobotsClientInterface clientInterface) {
		return spawnAI(connectionId, gameId, name, auth, clientInterface, Robot.class.getName());
	}
		
	public long spawnAI(long connectionId, long gameId, String name, String auth, RobotsClientInterface clientInterface, String entityClass) {
		if (name == null || name.isEmpty())
			return ERR_INVALID_ENTITY_NAME;
		
		if (entityClass == null || entityClass.isEmpty())
			return ERR_INVALID_ENTITY_TYPE;
		
		Game game = findGame(gameId);
		if (game == null)
			return ERR_GAME_NOT_FOUND;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return ERR_INVALID_PASSWORD;
		
		ClientTracker tracker = getTracker(connectionId, clientInterface);
		if (tracker.getEntityTypeCount(gameId, entityClass) >= game.getLevel().getSpawnableEntityCount(entityClass)) {
			return ERR_ENTITY_LIMIT_EXCEEDED;
		}
		
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
			tracker.addAI(gameId, entity.getUUID(), entityClass);
			LOG.info("spawned AI '{}':{} in game '{}':{}", name, entity.getUUID(), game.getName(), gameId);
			return entity.getUUID();
		} else {
			LOG.warn("failed to spawn AI '{}':{} in game '{}':{} using auth '{}'", name, entity.getUUID(), game.getName(), gameId, auth);
			return ERR_SPAWN_DENIED;
		}
	}
	
	public boolean despawnAI(long connectionId, long gameId, long entityUUID, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		List<Tickable> possibleAIs = game.getTickales(t -> t instanceof AI && ((AI) t).getEntity().getUUID() == entityUUID);
		if (possibleAIs.isEmpty())
			return false;
		
		ClientTracker tracker = getTracker(connectionId, clientInterface);
		
		AI ai = (AI) possibleAIs.get(0);
		AIDespawnEvent event = new AIDespawnEvent(game.getWorld(), ai.getEntity(), ai);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful()) {
			tracker.removeAI(gameId, entityUUID);
			LOG.info("{}:{} was removed from game '{}':{}", ai.getEntity().getName(), ai.getEntity().getUUID(), game.getName(), game.getUUID());
		} else {
			LOG.warn("{} could not be removed from game '{}':{}", entityUUID, game.getName(), game.getUUID());
		}
		
		return event.isSuccessful();
	}
	
	public boolean observeWorld(long connectionId, long gameId, String auth, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return false;
		
		ClientTracker tracker = getTracker(connectionId, clientInterface);
		
		ObserverJoinEvent event = new ObserverJoinEvent(clientInterface);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful()) {
			tracker.addObserver(gameId);
			LOG.info("added observer for game '{}':{}", game.getName(), gameId);
		} else {
			LOG.warn("failed to add observer for game '{}':{} with auth '{}'", game.getName(), gameId, auth);
		}
		
		return event.isSuccessful();
	}
	
	public boolean unobserveWorld(long connectionId, long gameId, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		List<Tickable> possibleObservers = game.getTickales(t -> (t instanceof WorldObserver) && ((WorldObserver) t).getClientInterface() == clientInterface);
		if (possibleObservers.isEmpty())
			return false;
		
		ClientTracker tracker = getTracker(connectionId, clientInterface);
		
		WorldObserver observer = (WorldObserver) possibleObservers.get(0);
		ObserverLeftEvent event = new ObserverLeftEvent(observer);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful()) {
			tracker.removeObserver(gameId);
			LOG.info("removed observer from game '{}':{}", game.getName(), gameId);
		} else {
			LOG.warn("failed to locate observer in game '{}':{}", game.getName(), gameId);
		}
		
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
		
		synchronized (clientTrackers) {
			clientTrackers.values().forEach(tracker -> tracker.onDisconnect());
		}
		
		LOG.info("server closed");
	}
	
	protected ClientTracker getTracker(long connectionId, RobotsClientInterface clientInterface) {
		synchronized (clientTrackers) {
			ClientTracker tracker = clientTrackers.get(connectionId);
			
			if (tracker == null) {
				tracker = new ClientTracker(connectionId, clientInterface);
					clientTrackers.put(connectionId, tracker);
			}
		
			return tracker;
		}
	}
	
	public void onConnect(long connectionId) {
		// nothing to do here
	}
	
	public void onDisconnect(long connectionId) {
		ClientTracker tracker = null;
		synchronized (clientTrackers) {
			tracker = clientTrackers.remove(connectionId);
		}
		
		if (tracker != null)
			tracker.onDisconnect();
	}
	
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	private final class ClientTracker {
		private final long connectionId;
		private final RobotsClientInterface clientInterface;
		/** game -> (entityType -> Count) */
		private final Map<Long, Map<String, Integer>> gameTypeCounter = new HashMap<>();
		private final List<ObserverData> observers = new ArrayList<>();
		private final List<AIData> ais = new ArrayList<>();
		
		private ClientTracker(long connectionId, RobotsClientInterface clientInterface) {
			this.connectionId = connectionId;
			this.clientInterface = clientInterface;
		}
		
		private int getEntityTypeCount(long gameId, String entityClass) {
			synchronized (gameTypeCounter) {
				Map<String, Integer> typeCounter = gameTypeCounter.get(gameId);
				if (typeCounter != null) {
					Integer counter = typeCounter.get(entityClass);
					if (counter != null)
						return counter;
				}
				
				return 0;
			}
		}
		
		private void addObserver(long gameId) {
			synchronized (observers) {
				observers.add(new ObserverData(gameId));
			}
		}
		
		private void removeObserver(long gameId) {
			synchronized (observers) {
				Iterator<ObserverData> it = observers.iterator();
				while (it.hasNext()) {
					if (it.next().match(gameId)) {
						it.remove();
						return;
					}
				}
			}
		}
		
		private void addAI(long gId, long eId, String entityClass) {
			synchronized (ais) {
				ais.add(new AIData(gId, eId, entityClass));
			}
			synchronized (gameTypeCounter) {
				Map<String, Integer> typeCounter = gameTypeCounter.get(gId);
				if (typeCounter == null) {
					typeCounter = new HashMap<>();
					gameTypeCounter.put(gId, typeCounter);
				}
				
				Integer counter = typeCounter.get(entityClass);
				if (counter == null) {
					counter = 0;
					typeCounter.put(entityClass, counter);
				}
				
				counter++;
			}
		}
		
		private void removeAI(long gId, long eId) {
			String entityClass = null;
			synchronized (ais) {
				Iterator<AIData> it = ais.iterator();
				while (it.hasNext()) {
					AIData ai = it.next();
					if (ai.match(gId, eId)) {
						it.remove();
						entityClass = ai.entityClass;
						break;
					}
				}
			}
			
			synchronized (gameTypeCounter) {
				Map<String, Integer> typeCounter = gameTypeCounter.get(gId);
				if (typeCounter != null) {
					Integer counter = typeCounter.get(entityClass);
					if (counter != null)
						counter--;
				}
			}
		}
		
		public void onDisconnect() {
			synchronized (ais) {
				for (AIData ai : new ArrayList<>(ais))
					despawnAI(connectionId, ai.gId, ai.eId, clientInterface);
				
				ais.clear();
			}
			synchronized (LOG) {
				for (ObserverData observer : new ArrayList<>(observers))
					unobserveWorld(connectionId, observer.gameId, clientInterface);
				
				observers.clear();
			}
		}
	}
	
	protected static class ObserverData {
		protected final long gameId;
		
		protected ObserverData(long gameId) {
			this.gameId = gameId;
		}
		
		protected boolean match(long gId) {
			return this.gameId == gId;
		}
	}

	protected static class AIData {
		protected final long gId;
		protected final long eId;
		protected final String entityClass;
		
		protected AIData(long gId, long eId, String entityClass) {
			this.gId = gId;
			this.eId = eId;
			this.entityClass = entityClass;
		}
		
		protected boolean match(long gId, long eId) {
			return this.gId == gId && this.eId == eId;
		}
	}
}
