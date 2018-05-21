package com.github.schnupperstudium.robots.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.RobotsServerInterface;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public abstract class RobotsClient {	
	protected final Map<Long, AbstractAI> ais = new HashMap<>();
	protected final Map<Long, IWorldObserver> observers = new HashMap<>();
	
	protected RobotsServerInterface serverInterface;
	
	protected RobotsClient() {

	}
	
	public abstract RobotsClientInterface createClientInterface();
	
	public List<GameInfo> listGames() {
		return serverInterface.listGames();
	}
	
	public boolean spawnAI(long gameId, String aiName, String auth, Class<? extends AbstractAI> aiClass) {
		try {
			final Constructor<? extends AbstractAI> constructor = aiClass.getConstructor(long.class);
			
			return spawnAI(gameId, aiName, auth, (uuid) -> {
				try {
					return constructor.newInstance(uuid);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					System.out.println("Failed to create instance from " + aiClass.getName() + ": " + e.getMessage());
					return null;
				}
			});
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println("No valid constructor found for (long): " + aiClass.getName() + " -- " + e.getMessage());
			return false;
		}
	}
	
	public boolean spawnAI(long gameId, String aiName, String auth, AIFactory aiFactory) {
		final long uuid = serverInterface.spawnEntity(gameId, aiName, auth);
		if (uuid < 0) {
			System.out.println("AI spawn failed with code: " + uuid);
			return false;
		}
		
		AbstractAI ai = aiFactory.createAI(uuid);
		if (ai == null) {
			System.out.println("Failed to create ai with uuid: " + uuid);
			return false;
		}
		
		synchronized (ais) {
			ais.put(uuid, ai);		
		}
		
		return true;
	}
	
	public boolean spawnObserver(long gameId, String auth, IWorldObserver observer) {		
		boolean observable = serverInterface.observerWorld(gameId, auth);
		if (!observable)
			return false;
		
		synchronized (observers) {
			observers.put(gameId, Objects.requireNonNull(observer));
		}
		
		return true;
	}
	
	public List<Level> listLevels() {
		return serverInterface.listLevels();
	}
	
	protected EntityAction makeTurn(long uuid) {
		AbstractAI ai = ais.get(uuid);
		
		// ai not found
		if (ai == null)
			return NoAction.INSTANCE;
		
		EntityAction action = ai.makeTurn();
		if (action == null)
			return NoAction.INSTANCE;
		else
			return action;
	}
	
	protected void updateVision(long uuid, List<Tile> vision) {
		AbstractAI ai = ais.get(uuid);
		if (ai != null) 
			ai.updateVision(vision);
	}
	
	protected void updateEntity(long uuid, Entity entity) {
		AbstractAI ai = ais.get(uuid);
		if (ai != null) 
			ai.updateEntity(entity);
	}
	
	protected void updateObserver(long gameId, World world) {
		IWorldObserver observer = observers.get(gameId);
		if (observer != null)
			observer.updateWorld(gameId, world);
	}
	
	public RobotsServerInterface getServerInterface() {
		return serverInterface;
	}
	
	public void close() throws IOException {
		// TODO: think about things to do :D
	}
}
