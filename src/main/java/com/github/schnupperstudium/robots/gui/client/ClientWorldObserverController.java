package com.github.schnupperstudium.robots.gui.client;

import com.github.schnupperstudium.robots.client.IWorldObserver;
import com.github.schnupperstudium.robots.gui.ObserverViewController;
import com.github.schnupperstudium.robots.gui.SimpleRenderer;
import com.github.schnupperstudium.robots.world.World;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

public class ClientWorldObserverController extends ObserverViewController implements IWorldObserver {
	
	public ClientWorldObserverController() {
		
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
	}
}
