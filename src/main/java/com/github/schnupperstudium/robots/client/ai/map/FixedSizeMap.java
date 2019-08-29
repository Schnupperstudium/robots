package com.github.schnupperstudium.robots.client.ai.map;

import java.util.List;

import com.github.schnupperstudium.robots.world.Map;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

public class FixedSizeMap implements Map {
	private final int width;
	private final int height;
	private final Tile[][] tiles;
	
	public FixedSizeMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y] = new Tile(x, y, Material.UNDEFINED, null, null);
			}
		}
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Tile getTile(int x, int y) {
		if (inBounds(x, y)) {
			return tiles[x][y];
		} else {
			return new Tile(null, x, y, Material.UNDEFINED);
		}
	}

	public void updateMap(List<Tile> tiles) {
		if (tiles == null)
			return;
		
		tiles.forEach(this::updateTile);
	}
	
	public void updateTile(Tile tile) {
		if (tile == null && inBounds(tile)) {
			return;
		}
		
		Tile t = getTile(tile.getX(), tile.getY());
		t.setMaterial(tile.getMaterial());
		t.setVisitor(tile.getVisitor());
		t.setItem(tile.getItem());
	}
	
	public boolean inBounds(Tile t) {
		return inBounds(t.getX(), t.getY());
	}
	
	public boolean inBounds(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
}
