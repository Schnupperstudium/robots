package com.github.schnupperstudium.robots.client.ai.map;

import java.util.List;

import com.github.schnupperstudium.robots.world.Map;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

public class ScalingMap implements Map {
	private Tile[][] mapTiles;
	private int width;
	private int height;
	
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	
	public ScalingMap() {
		this.width = 0;
		this.height = 0;
		this.minX = Integer.MAX_VALUE;
		this.maxX = Integer.MIN_VALUE;
		this.minY = Integer.MAX_VALUE;
		this.maxY = Integer.MIN_VALUE;
		this.mapTiles = new Tile[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.mapTiles[x][y] = new Tile(null, x, y, Material.UNDEFINED);
			}
		}
	}
	
	public void updateMap(List<Tile> tiles) {
		if (tiles == null || tiles.isEmpty())
			return;
		
		final int vMinX = tiles.stream().map(Tile::getX).min(Integer::compare).get();
		final int vMinY = tiles.stream().map(Tile::getY).min(Integer::compare).get();
		final int vMaxX = tiles.stream().map(Tile::getX).max(Integer::compare).get();
		final int vMaxY = tiles.stream().map(Tile::getY).max(Integer::compare).get();
		
		if (!isWithinMap(vMinX, vMinY) || !isWithinMap(vMaxX, vMaxY)) 
			resizeMap(Math.min(this.minX, vMinX), Math.min(this.minY, vMinY), Math.max(this.maxX, vMaxX + 1), Math.max(this.maxY, vMaxY + 1));
		
		tiles.forEach(this::updateTile);
	}
	
	private void resizeMap(int nMinX, int nMinY, int nMaxX, int nMaxY) {
		int nWidth = nMaxX - nMinX;
		int nHeight = nMaxY - nMinY;
		Tile[][] nTiles = new Tile[nWidth][nHeight];
		for (int i = 0; i < nWidth; i++) {
			int x = i + nMinX;
			for (int j = 0; j < nHeight; j++) {
				int y = j + nMinY;
				if (isWithinMap(x, y))
					nTiles[i][j] = mapTiles[x - minX][y - minY];
				else
					nTiles[i][j] = new Tile(null, x, y, Material.UNDEFINED);
			}
		}
		
		this.mapTiles = nTiles;
		this.minX = nMinX;
		this.minY = nMinY;
		this.maxX = nMaxX;
		this.maxY = nMaxY;
		this.width = nWidth;
		this.height = nHeight;
	}
	
	private void updateTile(Tile tile) {
		int x = tile.getX();
		int y = tile.getY();
		
		Tile mapTile = mapTiles[x - minX][y - minY];
		mapTile.setMaterial(tile.getMaterial());
		mapTile.setVisitor(tile.getVisitor());
		mapTile.setItem(tile.getItem());
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
		if (isWithinMap(x, y))
			return mapTiles[x - minX][y - minY];
		else
			return new Tile(null, x, y, Material.UNDEFINED);
	}
	
	@Override
	public int getMinX() {
		return minX;
	}
	
	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMinY() {
		return minY;
	}
	
	@Override
	public int getMaxY() {
		return maxY;
	}
	
	public boolean isWithinMap(int x, int y) {
		return x >= minX && x < maxX && y >= minY && y < maxY;
	}
}
