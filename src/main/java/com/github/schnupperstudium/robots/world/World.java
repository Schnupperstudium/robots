package com.github.schnupperstudium.robots.world;

import java.util.ArrayList;
import java.util.List;

public class World {
	private final Tile[][] fields;
	private final List<Tile> spawns = new ArrayList<>();
	private final int width;
	private final int height;
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		this.fields = new Tile[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				fields[x][y] = new Tile(x, y, Material.VOID);
			}
		}
	}
	
	public Tile getField(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) 
			return new Tile(x, y, Material.VOID);
		
		return fields[x][y];
	}
	
	public void setMaterial(int x, int y, Material material) {
		getField(x, y).setMaterial(material);
	}
	
	public List<Tile> getSpawns() {
		return spawns;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
