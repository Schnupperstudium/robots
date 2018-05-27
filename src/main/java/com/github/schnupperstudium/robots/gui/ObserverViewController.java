package com.github.schnupperstudium.robots.gui;

import com.github.schnupperstudium.robots.events.server.RoundCompleteEvent;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.World;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;

public class ObserverViewController {
	private final long gameId;
	
	@FXML 
	private AnchorPane worldCanvasAnchor;
	
	private Canvas worldCanvas;
	
	public ObserverViewController(Game game) {
		this.gameId = game.getUUID();		
		
		game.getEventDispatcher().registerListener(RoundCompleteEvent.class, this::roundCompleteEvent);
	}
	
	@FXML
	public void initialize() {
		worldCanvas = new Canvas();
		worldCanvas.widthProperty().bind(worldCanvasAnchor.widthProperty());
		worldCanvas.heightProperty().bind(worldCanvasAnchor.heightProperty());
		worldCanvasAnchor.getChildren().add(worldCanvas);
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
