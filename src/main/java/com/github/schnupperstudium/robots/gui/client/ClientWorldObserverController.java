package com.github.schnupperstudium.robots.gui.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.client.IWorldObserver;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.entity.InventoryHolder;
import com.github.schnupperstudium.robots.gui.ObserverViewController;
import com.github.schnupperstudium.robots.gui.SimpleRenderer;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

public class ClientWorldObserverController extends ObserverViewController implements IWorldObserver {
	private final RobotsClient client;
	private final long gameId;
	
	public ClientWorldObserverController(RobotsClient client, long gameId) {
		this.client = client;
		this.gameId = gameId;
	}

	private void updateWorld(World world) {
		if (worldCanvas == null)
			return;
				
		GraphicsContext gc = worldCanvas.getGraphicsContext2D();		
		gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
		
		int tileSize = (int) Math.floor(Math.min(worldCanvas.getWidth(), worldCanvas.getHeight()) / Math.max(world.getWidth(), world.getHeight()));
		SimpleRenderer.renderWorld(gc, world, tileSize);
	}

	@Override
	public void updateWorld(long gameId, World world) {
		Platform.runLater(() -> updateWorld(world));
		
		List<InventoryHolder> inventoryHolders = new ArrayList<>();
		for (int x = 0; x < world.getWidth(); x++) {
			for (int y = 0; y < world.getHeight(); y++) {
				Tile tile = world.getTile(x, y);
				if (tile.hasVisitor() && tile.getVisitor() instanceof InventoryHolder) {
					inventoryHolders.add((InventoryHolder) tile.getVisitor());
				}
			}
		}
		
		updateInventories(inventoryHolders);
	}
	
	public void shutdown() {
		client.despawnObserver(gameId);
	}
}