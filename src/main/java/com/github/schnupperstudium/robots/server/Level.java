package com.github.schnupperstudium.robots.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.io.MapFileParser;
import com.github.schnupperstudium.robots.server.module.GameModule;
import com.github.schnupperstudium.robots.world.World;
import com.github.schnupperstudium.robots.world.WorldLoader;
import com.google.gson.JsonElement;

public class Level {
	private static final Logger LOG = LogManager.getLogger();
	private static final WorldLoader DEFAULT_LOADER = new MapFileParser();
	
	private String name;
	private Map<String, JsonElement> modules;
	private String mapLoader;
	private String mapLocation;
	private String desc;
	private Map<String, Integer> spawnableEntities; 
	
	protected Level() {
		// constructor for kryo
	}
	
	public Level(String name, String mapLocation, String desc) {
		this(name, new HashMap<>(), null, mapLocation, desc, new HashMap<>());
	}
	
	public Level(String name, Map<String, JsonElement> modules, String mapLoader, String mapLocation, String desc, Map<String, Integer> spawnableEntities) {
		this.name = name;
		this.modules = modules;
		this.mapLoader = mapLoader;
		this.mapLocation = mapLocation;
		this.desc = desc;
		this.spawnableEntities = spawnableEntities;
	}

	public String getName() {
		return name;
	}
	
	public Map<String, JsonElement> getModules() {
		return modules;
	}
	
	public String getMapLoader() {
		return mapLoader;
	}
	
	public String getMapLocation() {
		return mapLocation;
	}
	
	public String getDesc() {
		return desc;
	}

	public List<String> getSpawnableEntities() {
		return new ArrayList<>(spawnableEntities.keySet());
	}
	
	public int getSpawnableEntityCount(Class<? extends Entity> entityClass) {
		if (entityClass == null)
			return 0;
		else
			return getSpawnableEntityCount(entityClass.getName());
	}
	
	public int getSpawnableEntityCount(String name) {
		Integer count = spawnableEntities.get(name);
		if (count == null)
			return 0;
		else 
			return count;
	}
	
	public List<GameModule> loadModules() {
		List<GameModule> modules = new ArrayList<>();
		getModules().entrySet().stream().map(entry -> {
			try {
				Class<?> clazz = Class.forName(entry.getKey());
				if (!GameModule.class.isAssignableFrom(clazz)) {					
					return null;
				}
				
				Class<? extends GameModule> moduleClazz = clazz.asSubclass(GameModule.class);
				
				JsonElement element = entry.getValue();
				if (element == null || element.isJsonNull()) {
					Constructor<? extends GameModule> constructor = moduleClazz.getConstructor();
					return constructor.newInstance();
				} else {
					Constructor<? extends GameModule> constructor = moduleClazz.getConstructor(JsonElement.class);
					return constructor.newInstance(element);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
					| IllegalArgumentException | InvocationTargetException | NoSuchMethodException 
					| SecurityException e) {
				LOG.catching(e);
				return null;
			}			
		}).filter(module -> module != null).forEach(modules::add);
		
		return modules;
	}
	
	public World loadWorld() throws IOException {
		InputStream is = Level.class.getResourceAsStream(mapLocation);
		if (is == null)
			throw new FileNotFoundException("file was not found at: " + mapLocation);
		
		WorldLoader loader = DEFAULT_LOADER;
		if (mapLoader != null && !mapLoader.isEmpty()) {
			try {
				Class<?> clazz = Class.forName(mapLoader);
				loader = clazz.asSubclass(WorldLoader.class).getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new IOException(e);
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
		}
		
		return loader.loadWorld(is);
	}

	@Override
	public String toString() {
		return "Level [name=" + name + ", modules=" + modules + ", mapLoader=" + mapLoader + ", mapLocation="
				+ mapLocation + ", desc=" + desc + "]";
	}
}
