package com.github.schnupperstudium.robots.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

import com.github.schnupperstudium.robots.io.WorldParser;
import com.github.schnupperstudium.robots.world.World;

public class Level {
	private final String name;
	private final String gameClass;
	private final String mapLocation;
	private final String desc;
	
	public Level(String name, String gameClass, String location, String desc) {
		this.name = name;
		this.gameClass = gameClass;
		this.mapLocation = location;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	
	public String getGameClass() {
		return gameClass;
	}
	
	public String getMapLocation() {
		return mapLocation;
	}
	
	public String getDesc() {
		return desc;
	}

	public World loadWorld() throws FileNotFoundException, URISyntaxException {
		URL url = Level.class.getResource(mapLocation);
		if (url == null)
			throw new FileNotFoundException("file was not found at: " + mapLocation);
		
		World world = WorldParser.fromFile(new File(url.toURI()));
		return world;
	}
	
	@Override
	public String toString() {
		return "Level [name=" + name + ", gameClass=" + gameClass + ", mapLocation=" + mapLocation + ", desc=" + desc
				+ "]";
	}
}
