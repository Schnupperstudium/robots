package com.github.schnupperstudium.robots.gui.client;

import java.util.List;

import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.EntityObserver;
import com.github.schnupperstudium.robots.client.VisionObserver;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.gui.ObserverViewController;
import com.github.schnupperstudium.robots.gui.SimpleRenderer;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.application.Platform;

public class ClientAIObserverViewController extends ObserverViewController implements VisionObserver, EntityObserver {
	private final AbstractAI ai;
	
	private List<Tile> vision = null;
	private Entity entity = null;
	
	public ClientAIObserverViewController(AbstractAI ai) {
		super();
		
		this.ai = ai;
		ai.addEntityUpdateObserver(this);
		ai.addVisionObserver(this);
	}

	@Override
	public void onEntityUpdate(AbstractAI ai, Entity entity) {
		this.entity = entity;
	}

	@Override
	public void onVisionUpdate(AbstractAI ai, List<Tile> vision) {
		this.vision = vision;
		
		Platform.runLater(this::renderVision);
	}
	
	private void renderVision() {
		if (vision == null || vision.isEmpty())
			return;
		
		final int minX = vision.stream().map(t -> t.getX()).min(Integer::compareTo).get();
		final int maxX = vision.stream().map(t -> t.getX()).max(Integer::compareTo).get();
		final int minY = vision.stream().map(t -> t.getY()).min(Integer::compareTo).get();
		final int maxY = vision.stream().map(t -> t.getY()).max(Integer::compareTo).get();
		final int visionWidth = maxX - minX;
		final int visionHeight = maxY - minY;
		
		int tileSize = (int) Math.floor(Math.min(worldCanvas.getWidth(), worldCanvas.getHeight()) / Math.max(visionWidth, visionHeight));
		SimpleRenderer.renderTilesCompact(worldCanvas.getGraphicsContext2D(), vision, tileSize);
	}
}
