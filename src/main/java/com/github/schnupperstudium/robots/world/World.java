package com.github.schnupperstudium.robots.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class World {
	private final Tile[][] tiles;
	private transient final Set<Tile> spawns = new HashSet<>();
	private final int width;
	private final int height;
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y] = new Tile(this, x, y, Material.VOID);
			}
		}
	}
	
	public World(int width, int height, Collection<Tile> tileCollection) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		
		// use given tiles
		for (Tile tile : tileCollection) {
			tiles[tile.getX()][tile.getY()] = new Tile(this, tile);
		}
		
		// fill missing ones
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (tiles[x][y] == null)
					tiles[x][y] = new Tile(this, x, y, Material.VOID);
			}
		}
	}
	
	public Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) 
			return new Tile(this, x, y, Material.VOID);
		
		return tiles[x][y];
	}
	
	public void setMaterial(int x, int y, Material material) {
		getTile(x, y).setMaterial(material);
	}
	
	public List<Tile> getSpawns() {
		synchronized (spawns) {
			return new ArrayList<>(spawns);
		}
	}

	protected void addSpawn(Tile tile) {
		synchronized (spawns) {
			spawns.add(tile);
		}
	}
	
	protected void removeSpawn(Tile tile) {
		synchronized (spawns) {
			spawns.remove(tile);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
