package com.github.schnupperstudium.robots.gui.client;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.EntityObserver;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.client.VisionObserver;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.gui.ObserverViewController;
import com.github.schnupperstudium.robots.gui.SimpleRenderer;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.application.Platform;

public class ClientAIObserverViewController extends ObserverViewController implements VisionObserver, EntityObserver {
	private final RobotsClient client;
	private final AbstractAI ai;
	
	private List<Tile> vision = null;
//	private Entity entity = null;
	
	public ClientAIObserverViewController(RobotsClient client, AbstractAI ai) {		
		super();
		
		this.client = client;
		this.ai = ai;
		ai.addEntityUpdateObserver(this);
		ai.addVisionObserver(this);
	}

	@Override
	public void onEntityUpdate(AbstractAI ai, Entity entity) {
//		this.entity = entity;
	}

	@Override
	public void onVisionUpdate(AbstractAI ai, List<Tile> vision) {
		this.vision = vision;
		
		Platform.runLater(this::renderVision);
		
		List<Entity> inventoryHolders = new ArrayList<>();
		for (Tile tile : vision) {
			if (tile.hasVisitor() && tile.getVisitor().hasInventory()) {
				inventoryHolders.add(tile.getVisitor());			
			}
		}
		
		updateInventories(inventoryHolders);
	}
	
	private void renderVision() {
		if (vision == null || vision.isEmpty())
			return;		
		
		final int minX = vision.stream().map(t -> t.getX()).min(Integer::compareTo).get();
		final int maxX = vision.stream().map(t -> t.getX()).max(Integer::compareTo).get();
		final int minY = vision.stream().map(t -> t.getY()).min(Integer::compareTo).get();
		final int maxY = vision.stream().map(t -> t.getY()).max(Integer::compareTo).get();
		final int visionWidth = maxX - minX + 1;
		final int visionHeight = maxY - minY + 1;
		
		clearCanvas();
		double tileSize = Math.min(worldCanvas.getWidth() / visionWidth, worldCanvas.getHeight() / visionHeight);
		SimpleRenderer.renderTilesCompact(worldCanvas.getGraphicsContext2D(), vision, tileSize);
	}
	
	public void shutdown() {
		// this method must be called when the window for the AI is closed
		if (ai != null)
			client.despawnAI(ai);
	}
}
