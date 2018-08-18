package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.io.LevelParser;
import com.github.schnupperstudium.robots.server.event.MasterServerListener;
import com.github.schnupperstudium.robots.server.tickable.AI;
import com.github.schnupperstudium.robots.server.tickable.Tickable;
import com.github.schnupperstudium.robots.server.tickable.WorldObserver;
import com.github.schnupperstudium.robots.world.Tile;

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
	public static final long ERR_SPAWN_OCCUPIED = -12;
	
	private static final Logger LOG = LogManager.getLogger();
	
	protected final Map<Long, ClientTracker> clientTrackers = new HashMap<>();
	protected final Map<String, LivingEntityFactory> entityFactories = new HashMap<>();
	protected final MasterServerListener masterServerListener = new MasterServerListener();
	protected final List<Game> games = new ArrayList<>();
	protected final List<Level> availableLevels = new ArrayList<>();

	private boolean run = true;
	
	public RobotsServer() throws IOException {
		loadLevels();
		loadEntityFactories();
	}
		
	private void loadEntityFactories() {
		// factory for every playable class
		entityFactories.put(Robot.class.getName(), (game, x, y, name) -> { Robot robot = new Robot(name); robot.setPosition(x, y); return robot; });
	}
	
	private void loadLevels() throws IOException {
		availableLevels.clear();
		
		InputStream levelListIS = getClass().getResourceAsStream("/level/levels.list");
		List<String> levelNames = new ArrayList<>();
		Scanner scanner = new Scanner(levelListIS);
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line != null && !line.isEmpty())
				levelNames.add(line);
		}
		
		scanner.close();
		
		for (String levelName : levelNames) {
			InputStream is = getClass().getResourceAsStream("/level/" + levelName);
			if (is == null) {
				LOG.warn("level not found: /level/" + levelName);
				continue;
			}
			Level l = LevelParser.loadLevel(is);
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
			LOG.catching(e);
			return ERR_FAILED_GAME_START;
		}
		
		boolean canStart = getMasterServerListener().canGameStart(this, game);		
		if (!canStart) {
			return ERR_GAME_START_DENIED;
		}
		
		addGame(game);
		
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
		AI ai = new AI(game, clientInterface, entity);
		
		// check listeners
		boolean canEntitySpawn = game.getMasterGameListener().canEntitySpawn(game, entity);
		boolean canAISpawn = game.getMasterGameListener().canAISpawn(game, ai);
		
		if (canEntitySpawn && canAISpawn) {
			// attempt to spawn entity and ai
			Tile tile = entity.getTile(game.getWorld());
			if (!tile.canVisit())
				return ERR_SPAWN_OCCUPIED;
			
			tile.setVisitor(entity);
			game.addTickable(ai);
			tracker.addAI(gameId, entity.getUUID(), entityClass);
			
			// notify listeners
			game.getMasterGameListener().onEntitySpawn(game, entity);
			game.getMasterGameListener().onAISpawn(game, ai);
			
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
		LivingEntity entity = ai.getEntity();

		Tile tile = entity.getTile(game.getWorld());
		if (tile.getVisitor() != entity)
			return false;
		
		tile.setVisitor(null);
		game.removeTickable(ai);

		tracker.removeAI(gameId, entityUUID);
		
		game.getMasterGameListener().onEntityDespawn(game, entity);
		game.getMasterGameListener().onAIDespawn(game, ai);
		LOG.info("{}:{} was removed from game '{}':{}", ai.getEntity().getName(), ai.getEntity().getUUID(), game.getName(), game.getUUID());
		
		return true;
	}
	
	public boolean observeWorld(long connectionId, long gameId, String auth, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return false;
		
		ClientTracker tracker = getTracker(connectionId, clientInterface);
		WorldObserver observer = new WorldObserver(clientInterface);
		boolean canObserverJoin = game.getMasterGameListener().canObserverJoin(game, observer);
		
		if (canObserverJoin) {
			game.addTickable(observer);
			tracker.addObserver(gameId);
			game.getMasterGameListener().onObserverJoin(game, observer);
			LOG.info("added observer for game '{}':{}", game.getName(), gameId);
		} else {
			LOG.warn("failed to add observer for game '{}':{} with auth '{}'", game.getName(), gameId, auth);
		}
		
		return canObserverJoin;
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
		game.removeTickable(observer);
		tracker.removeObserver(gameId);

		game.getMasterGameListener().onObserverQuit(game, observer);		
		LOG.info("removed observer from game '{}':{}", game.getName(), gameId);

		return true;
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
	
	public MasterServerListener getMasterServerListener() {
		return masterServerListener;
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
			int result = 0;
			synchronized (gameTypeCounter) {
				Map<String, Integer> typeCounter = gameTypeCounter.get(gameId);
				if (typeCounter != null) {
					Integer counter = typeCounter.get(entityClass);
					if (counter != null)
						result = counter;
				}
			}
			
			LOG.debug("connection {} has {} entities of type '{}'", connectionId, result, entityClass);
			return result;
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
				
				Integer counter = typeCounter.getOrDefault(entityClass, 0) + 1;
				typeCounter.put(entityClass, counter);
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
	
	private static class ObserverData {
		private final long gameId;
		
		private ObserverData(long gameId) {
			this.gameId = gameId;
		}
		
		private boolean match(long gId) {
			return this.gameId == gId;
		}
	}

	private static class AIData {
		private final long gId;
		private final long eId;
		private final String entityClass;
		
		private AIData(long gId, long eId, String entityClass) {
			this.gId = gId;
			this.eId = eId;
			this.entityClass = entityClass;
		}
		
		private boolean match(long gId, long eId) {
			return this.gId == gId && this.eId == eId;
		}
	}
}
