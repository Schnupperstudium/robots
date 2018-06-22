package com.github.schnupperstudium.robots.gui.server;

import com.github.schnupperstudium.robots.gui.ObserverViewController;
import com.github.schnupperstudium.robots.gui.SimpleRenderer;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;
import com.github.schnupperstudium.robots.world.World;

import javafx.scene.canvas.GraphicsContext;

public class ServerWorldObserverController extends ObserverViewController {
	private final long gameId;
	
	public ServerWorldObserverController(Game game) {
		this.gameId = game.getUUID();
		
		game.getMasterGameListener().registerListener(new AbstractGameListener() {
			@Override
			public void onRoundComplete(Game game) {
				if (game.getUUID() == gameId) {
					updateWorld(game.getWorld());
				}
			}
		});
	}

	private void updateWorld(World world) {
		if (worldCanvas == null)
			return;
				
		GraphicsContext gc = worldCanvas.getGraphicsContext2D();		
		gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
		
		int tileSize = (int) Math.floor(Math.min(worldCanvas.getWidth(), worldCanvas.getHeight()) / Math.max(world.getWidth(), world.getHeight()));
		SimpleRenderer.renderWorld(gc, world, tileSize);
	}
}
