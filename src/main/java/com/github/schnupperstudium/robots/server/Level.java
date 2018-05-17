package com.github.schnupperstudium.robots.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import com.github.schnupperstudium.robots.io.MapFileParser;
import com.github.schnupperstudium.robots.world.World;
import com.github.schnupperstudium.robots.world.WorldLoader;

public class Level {
	private static final WorldLoader DEFAULT_LOADER = new MapFileParser();
	
	private final String name;
	private final String gameLoader;
	private final String mapLoader;
	private final String mapLocation;
	private final String desc;
	
	public Level(String name, String mapLocation, String desc) {
		this(name, null, null, mapLocation, desc);
	}
	
	public Level(String name, String gameLoader, String mapLoader, String mapLocation, String desc) {
		this.name = name;
		this.gameLoader = gameLoader;
		this.mapLoader = mapLoader;
		this.mapLocation = mapLocation;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	
	public String getGameLoader() {
		return gameLoader;
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
		return "Level [name=" + name + ", gameLoader=" + gameLoader + ", mapLoader=" + mapLoader + ", mapLocation="
				+ mapLocation + ", desc=" + desc + "]";
	}
}
