package com.github.schnupperstudium.robots.gui.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.gui.ObserverViewController;
import com.github.schnupperstudium.robots.gui.SimpleRenderer;
import com.github.schnupperstudium.robots.world.Map;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

public class ClientMapView extends ObserverViewController {
	
	public ClientMapView() {
		
	}

	private void updateWorld(Map map) {
		if (worldCanvas == null)
			return;
				
		GraphicsContext gc = worldCanvas.getGraphicsContext2D();		
		gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
		
		double tileSize = Math.min(worldCanvas.getWidth() / map.getWidth(), worldCanvas.getHeight() / map.getHeight());
		SimpleRenderer.renderMap(gc, map, tileSize);
	}

	public void updateMap(Map map) {
		Platform.runLater(() -> updateWorld(map));
		
		final int minX = map.getMinX();
		final int maxX = map.getMaxX();
		final int minY = map.getMinY();
		final int maxY = map.getMaxY();
		
		List<Entity> inventoryHolders = new ArrayList<>();
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				Tile tile = map.getTile(x, y);
				if (tile.hasVisitor() && tile.getVisitor().hasInventory()) {
					inventoryHolders.add(tile.getVisitor());
				}
			}
		}
		
		updateInventories(inventoryHolders);
	}
}
