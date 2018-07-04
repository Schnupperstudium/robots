package com.github.schnupperstudium.robots.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.RobotsServerInterface;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public abstract class RobotsClient {	
	private static final Logger LOG = LogManager.getLogger();
	
	protected final Map<Long, AbstractAI> ais = new HashMap<>();
	protected final Map<Long, IWorldObserver> observers = new HashMap<>();
	
	protected RobotsServerInterface serverInterface;
	
	protected RobotsClient() {

	}
	
	public List<GameInfo> listGames() {
		return serverInterface.listGames();
	}
	
	public AbstractAI spawnAI(long gameId, String aiName, String auth, Class<? extends AbstractAI> aiClass) {
		try {
			final Constructor<? extends AbstractAI> constructor = aiClass.getConstructor(RobotsClient.class, long.class, long.class);
			
			return spawnAI(gameId, aiName, auth, (client, gId, eId) -> {
				try {
					return constructor.newInstance(client, gId, eId);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Failed to create instance from " + aiClass.getName() + ": " + e.getMessage());
					return null;
				}
			});
		} catch (NoSuchMethodException | SecurityException e) {
			LOG.error("No valid constructor found for (long): " + aiClass.getName() + " -- " + e.getMessage());
			return null;
		}
	}
	
	public AbstractAI spawnAI(long gameId, String aiName, String auth, AIFactory aiFactory) {
		final long uuid = serverInterface.spawnEntity(gameId, aiName, auth);
		if (uuid < 0) {
			LOG.error("AI spawn failed with code: " + uuid);
			return null;
		}
		
		AbstractAI ai = aiFactory.createAI(this, gameId, uuid);
		if (ai == null) {
			LOG.error("Failed to create ai with uuid: " + uuid);
			return null;
		}
		
		synchronized (ais) {
			ais.put(uuid, ai);		
		}
		
		LOG.info("spawned AI '{}' in game {} using auth '{}'", aiName, gameId, auth);
		return ai;
	}
	
	public boolean despawnAI(AbstractAI ai) {
		if (ai == null)
			return false;
		
		final boolean removed = serverInterface.removeEntity(ai.getGameId(), ai.getEntityUUID());
		
		if (removed) {
			synchronized (ais) {
				ais.remove(ai.getEntityUUID());
			}
			
			LOG.info("despawned AI '{}' from game {}", ai.getEntity() != null ? ai.getEntity().getName() : "<none>", ai.getGameId());
		} else
			LOG.warn("failed to despawn AI '{}' from game {}", ai.getEntity() != null ? ai.getEntity().getName() : "<none>", ai.getGameId());
		
		return removed;
	}
	
	public boolean spawnObserver(long gameId, String auth, IWorldObserver observer) {
		if (observer == null)
			return false;

		final boolean observable = serverInterface.observerWorld(gameId, auth);
		if (!observable) {
			LOG.warn("Failed to observer game '{}' with auth '{}'", gameId, auth);
			return false;
		}
		
		synchronized (observers) {
			observers.put(gameId, Objects.requireNonNull(observer));
		}
		
		LOG.info("Observing game '{}' with auth '{}'", gameId, auth);
		return true;
	}
	
	public boolean despawnObserver(long gameId) {
		final boolean removed = serverInterface.stopObserving(gameId);
		
		if (removed) {
			synchronized (observers) {
				observers.remove(gameId);
			}
			
			LOG.info("removed observer from game {}", gameId);
		} else
			LOG.warn("failed to remove observer from game {}", gameId);
		
		return removed;
	}
	
	public List<Level> listLevels() {
		return serverInterface.listLevels();
	}
	
	protected EntityAction makeTurn(long uuid) {
		LOG.trace("compute action for {}", uuid);
		AbstractAI ai = ais.get(uuid);
		
		if (ai == null) {
			LOG.warn("AI not found: {}", uuid);
			return NoAction.INSTANCE;
		}
		EntityAction action;
		try {
			action = ai.makeTurn();
		} catch (Exception e) {
			LOG.catching(e);
			action = null;
		}
		if (action == null) {
			LOG.warn("AI returned null action: {}", uuid);
			return NoAction.INSTANCE;
		} else {
			return action;
		}
	}
	
	protected void updateVision(long uuid, List<Tile> vision) {		
		AbstractAI ai = ais.get(uuid);
		if (ai != null) {
//			LOG.trace("vision update {}: {}", uuid, vision);
			try {
				ai.updateVision(vision);
			} catch (Exception e) {
				LOG.catching(e);
			}
		}
	}
	
	protected void updateEntity(long uuid, Entity entity) {
		AbstractAI ai = ais.get(uuid);
		if (ai != null) { 
			LOG.trace("update entity {}: {}", uuid, entity);
			try {
				ai.updateEntity(entity);
			} catch (Exception e) {
				LOG.catching(e);
			}
		}
	}
	
	protected void updateObserver(long gameId, World world) {
		IWorldObserver observer = observers.get(gameId);
		if (observer != null) {
//			LOG.trace("update observer {}: {}", gameId, world);
			try {
				observer.updateWorld(gameId, world);
			} catch (Exception e) {
				LOG.catching(e);
			}
		}
	}
	
	public RobotsServerInterface getServerInterface() {
		return serverInterface;
	}
	
	public void close() throws IOException {
		// TODO: think about things to do :D
	}
}
