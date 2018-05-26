package com.github.schnupperstudium.robots.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.events.AbstractServerEvent;
import com.github.schnupperstudium.robots.events.entity.AISpawnEvent;
import com.github.schnupperstudium.robots.events.game.ObserverJoinEvent;
import com.github.schnupperstudium.robots.events.server.GameStartEvent;
import com.github.schnupperstudium.robots.io.LevelParser;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.thedwoon.event.EventDispatcher;
import com.github.thedwoon.event.EventPriority;
import com.github.thedwoon.event.SynchronizedEventDispatcher;

public abstract class RobotsServer implements Runnable {
	private static final Logger LOG = LogManager.getLogger();
	
	protected final EventDispatcher eventDispatcher = new SynchronizedEventDispatcher();
	protected final List<Game> games = new ArrayList<>();
	protected final List<Level> availableLevels = new ArrayList<>();

	private boolean run = true;
	
	public RobotsServer() throws IOException {
		eventDispatcher.registerListener(AbstractServerEvent.class, this::executeEvent, EventPriority.MONITOR, true);
		
		loadLevels();
	}
		
	private void executeEvent(AbstractServerEvent event) {
		event.executeEvent(this);
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
			return -1;
		else if (levelName == null || levelName.isEmpty())
			return -2;
		
		Level level = findLevel(levelName);
		if (level == null)
			return -4;
		
		Game game = null;
		try {
			game = new Game(this, name, level, auth);
		} catch (IOException e) {
			LOG.warn("failed to start game '{}', '{}': {}", name, level, e.getMessage());
			return -5;
		}
		
		GameStartEvent event = new GameStartEvent(game);
		eventDispatcher.dispatchEvent(event);
		if (!event.isSuccessful()) {
			return -6;
		}
		
		LOG.info("started game [id: {}, name: {}, level: {}, map: {}]", 
				game.getUUID(), name, levelName, game.getLevel().getMapLocation());
		return game.getUUID();
	}
	
	public long spawnAI(long gameId, String name, String auth, RobotsClientInterface clientInterface) {
		if (name == null || name.isEmpty())
			return -1;
		
		Game game = findGame(gameId);
		if (game == null)
			return -2;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return -3;
		
		List<Tile> spawnTiles = game.getWorld().getSpawns();
		Tile spawnTile = null;
		while (!spawnTiles.isEmpty() && spawnTile == null) {
			int index = (int) (Math.random() * spawnTiles.size());
			spawnTile = spawnTiles.remove(index);
			if (!spawnTile.canVisit())
				spawnTile = null;
		}
		if (spawnTile == null)
			return -4;
		
		Robot robot = new Robot(name);
		robot.setPosition(spawnTile.getX(), spawnTile.getY());
		AISpawnEvent event = new AISpawnEvent(game.getWorld(), robot, clientInterface);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful()) {
			LOG.info("spawned AI '{}' in game {}", name, gameId);
			return robot.getUUID();
		} else {
			LOG.warn("failed to spawn AI '{}' in game {} using auth '{}'", name, gameId, auth);
			return -5;
		}
	}
	
	public boolean observerWorld(long gameId, String auth, RobotsClientInterface clientInterface) {
		Game game = findGame(gameId);
		if (game == null)
			return false;
		
		if (game.hasPassword() && !game.getPassword().equals(auth))
			return false;
		
		ObserverJoinEvent event = new ObserverJoinEvent(clientInterface);
		game.getEventDispatcher().dispatchEvent(event);
		if (event.isSuccessful())
			LOG.info("added observer for game {}", gameId);
		else
			LOG.warn("failed to add observer for game {} with auth '{}'", game, auth);
		
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
