package com.github.schnupperstudium.robots.gui;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public final class SimpleRenderer {
	public static int TILE_SIZE = 32;

	private static final Logger LOG = LogManager.getLogger(); 
	
	private SimpleRenderer() {
		
	}

	public static void renderTilesCompact(GraphicsContext gc, Collection<Tile> tiles) {
		renderTilesCompact(gc, tiles, TILE_SIZE);
	}
	
	public static void renderTilesCompact(GraphicsContext gc, Collection<Tile> tiles, double tileSize) {
		if (tiles == null || tiles.isEmpty())
			return;
		
		final long start = System.nanoTime();
		final int minX = tiles.stream().map(t -> t.getX()).min(Integer::compareTo).get();
		final int minY = tiles.stream().map(t -> t.getY()).min(Integer::compareTo).get();
		
		for (Tile tile : tiles) {
			final double renderX = (tile.getX() - minX) * tileSize;
			final double renderY = (tile.getY() - minY) * tileSize;
			
			renderTile(gc, tile, renderX, renderY, tileSize);
		}
		
		final long end = System.nanoTime();
		LOG.trace("renderTilesCompact took {}ms", ((end - start) / 1000) / 1000.0);
	}
	
	public static void renderWorld(GraphicsContext gc, World world) {
		renderWorld(gc, world, TILE_SIZE);
	}
	
	public static void renderWorld(GraphicsContext gc, World world, double tileSize) {
		final long start = System.nanoTime();
		final int width = world.getWidth();
		final int height = world.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile tile = world.getTile(x, y);
				final double renderX = x * tileSize;
				final double renderY = y * tileSize;
				
				renderTile(gc, tile, renderX, renderY, tileSize);
			}
		}
		final long end = System.nanoTime();
		LOG.trace("renderWorld took {}ms", ((end - start) / 1000) / 1000.0);
	}
	
	public static void renderTile(GraphicsContext gc, Tile tile, double renderX, double renderY, double tileSize) {
		// draw texture background
		Image materialTexture = Texture.getTexture(tile.getMaterial());
		gc.drawImage(materialTexture, renderX, renderY, tileSize, tileSize);
		
		// draw item
		if (tile.hasItem()) {
			renderEntity(gc, tile.getItem(), renderX, renderY, tileSize, tile.getItem().getFacing());
		}
		
		// draw entity
		if (tile.hasVisitor()) {
			renderEntity(gc, tile.getVisitor(), renderX, renderY, tileSize, tile.getVisitor().getFacing());
		}
	}
	
	private static void renderEntity(GraphicsContext gc, Entity entity, double renderX, double renderY, double tileSize, Facing facing) {		
		int rotation = 0;
		switch (facing) {
		case NORTH:
			rotation = 0;
			break;
		case EAST:
			rotation = 90;
			break;
		case SOUTH:
			rotation = 180;
			break;
		case WEST:
			rotation = 270;
			break;
		default:
			throw new IllegalArgumentException("unkown facing:" + facing);
		}
		
		Image texture = Texture.getTexture(entity, rotation);
		gc.drawImage(texture, renderX, renderY, tileSize, tileSize);
	}
	
	public static void renderInventory(GraphicsContext gc, String name, Inventory inventory) {
		renderInventory(gc, name, inventory, 40, 4);
	}
	
	public static void renderInventory(GraphicsContext gc, String name, Inventory inventory, int tileSize, int tilesPerRow) {
		final Image slotTexture = Texture.getTexture("inventory_slot");
		
		gc.setFill(Color.BLACK);
		gc.fillText(name, 4, 16, 152);		
		List<Item> items = inventory.getItems();
		for (int i = 0; i < inventory.getSize(); i++) {
			int renderX = (i % tilesPerRow) * tileSize;
			int renderY = (i / tilesPerRow) * tileSize + 20;
			
			gc.drawImage(slotTexture, renderX, renderY, tileSize, tileSize);
			
			if (i < items.size()) {
				Item item = items.get(i);
				renderEntity(gc, item, renderX, renderY, tileSize, Facing.NORTH);
			}
		}
	}
}
