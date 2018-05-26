package com.github.schnupperstudium.robots.gui;

import java.util.Collection;

import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public final class SimpleRenderer {
	public static int TILE_SIZE = 50;
	
	private SimpleRenderer() {
		
	}

	public static void renderTilesCompact(GraphicsContext gc, Collection<Tile> tiles) {
		renderTilesCompact(gc, tiles, TILE_SIZE);
	}
	
	public static void renderTilesCompact(GraphicsContext gc, Collection<Tile> tiles, int tileSize) {
		if (tiles == null || tiles.isEmpty())
			return;
		
		final int minX = tiles.stream().map(t -> t.getX()).min(Integer::compareTo).get();
		final int minY = tiles.stream().map(t -> t.getY()).min(Integer::compareTo).get();
		
		for (Tile tile : tiles) {
			final int renderX = (tile.getX() - minX) * tileSize;
			final int renderY = (tile.getY() - minY) * tileSize;
			
			renderTile(gc, tile, renderX, renderY, tileSize);
		}
	}
	
	public static void renderWorld(GraphicsContext gc, World world) {
		renderWorld(gc, world, TILE_SIZE);
	}
	
	public static void renderWorld(GraphicsContext gc, World world, int tileSize) {
		final int width = world.getWidth();
		final int height = world.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile tile = world.getTile(x, y);
				final int renderX = x * tileSize;
				final int renderY = y * tileSize;
				
				renderTile(gc, tile, renderX, renderY, tileSize);
			}
		}
	}
	
	public static void renderTile(GraphicsContext gc, Tile tile, int renderX, int renderY, int tileSize) {
		// draw texture background
		Image materialTexture = Texture.getTexture(tile.getMaterial());
		gc.drawImage(materialTexture, renderX, renderY, tileSize, tileSize);
		
		// draw item
		if (tile.hasItem()) {
			Image itemTexutre = Texture.getTexture(tile.getItem());
			gc.drawImage(itemTexutre, renderX, renderY, tileSize, tileSize);
		}
		
		// draw entity
		if (tile.hasVisitor()) {
			Image visitorTexture = Texture.getTexture(tile.getVisitor());
			gc.drawImage(visitorTexture, renderX, renderY, tileSize, tileSize);
		}
	}
}
