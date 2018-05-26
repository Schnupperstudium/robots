package com.github.schnupperstudium.robots.gui;

import com.github.schnupperstudium.robots.events.server.RoundCompleteEvent;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.World;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class ObserverViewController {
	private final long gameId;
	
	@FXML
	private Canvas worldCanvas;
	
	public ObserverViewController(Game game) {
		this.gameId = game.getUUID();
		
		game.getEventDispatcher().registerListener(RoundCompleteEvent.class, this::roundCompleteEvent);
	}
	
	private void updateWorld(World world) {
		if (worldCanvas == null)
			return;
		
		GraphicsContext gc = worldCanvas.getGraphicsContext2D();		
		gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
		
		SimpleRenderer.renderWorld(gc, world);
	}
	
	private void roundCompleteEvent(RoundCompleteEvent event) {
		if (event.getGame().getUUID() == gameId) {
			updateWorld(event.getGame().getWorld());
		}
	}
}
