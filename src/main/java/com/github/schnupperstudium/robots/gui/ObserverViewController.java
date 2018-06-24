package com.github.schnupperstudium.robots.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.schnupperstudium.robots.entity.Entity;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ObserverViewController {
	
	private final Map<Long, InventoryCanvas> inventoryMap = new HashMap<>();

	@FXML 
	private AnchorPane worldCanvasAnchor;
	@FXML
	private VBox inventoryBox;
	
	protected Canvas worldCanvas;
	
	public ObserverViewController() {
		
	}
	
	@FXML
	public void initialize() {
		worldCanvas = new ResizableCanvas();
		worldCanvasAnchor.getChildren().add(worldCanvas);
	}

	protected void clearCanvas() {
		final GraphicsContext gc = worldCanvas.getGraphicsContext2D();
		final double width = worldCanvas.getWidth();
		final double height = worldCanvas.getHeight();
		
		gc.clearRect(0, 0, width, height);
	}
	
	public void updateInventories(List<Entity> inventoryHolders) {		
		// check for inventories to remove		
		Iterator<Entry<Long, InventoryCanvas>> it = inventoryMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, InventoryCanvas> entry = it.next();			
			boolean contained = false;
			for (Entity holder : inventoryHolders) {
				if (holder.getUUID() == entry.getValue().eId) {
					contained = true;
					break;
				}
			}
			
			if (!contained) {
				it.remove();
				Platform.runLater(() -> inventoryBox.getChildren().remove(entry.getValue()));
			}
		}
		
		// update or create inventories
		for (Entity holder : inventoryHolders) {
			updateInventory(holder);
		}
	}
	
	private void updateInventory(Entity entity) {
		InventoryCanvas invCanvas = inventoryMap.get(entity.getUUID());
		if (invCanvas == null) {
			final InventoryCanvas inventoryCanvas = new InventoryCanvas(entity.getUUID(), entity.getInventory().getSize());
			invCanvas = inventoryCanvas;
			inventoryMap.put(entity.getUUID(), invCanvas);
			Platform.runLater(() -> inventoryBox.getChildren().add(inventoryCanvas));
		}
		
		invCanvas.name = entity.getName();
		invCanvas.entity = entity;
		Platform.runLater(invCanvas::render);
	}
		
	private class ResizableCanvas extends Canvas {
		private ResizableCanvas() {
			super();
		}
		
		@Override
		public void resize(double width, double height) {
			setWidth(worldCanvasAnchor.getWidth());
			setHeight(worldCanvasAnchor.getHeight());
		}
		
		@Override
		public double prefWidth(double height) {
			return getWidth();
		}
		
		@Override
		public double prefHeight(double width) {
			return getHeight();
		}		
		
		@Override
		public boolean isResizable() {
			return true;
		}
	}
	
	private static class InventoryCanvas extends Canvas {
		private static final int NAME_SIZE_SPACING = 20;
		private static final double SLOTS_PER_ROW = 4;
		
		private final long eId;
		private String name;
		private Entity entity;
		
		private InventoryCanvas(long entityUUID, int slots) {
			this.eId = entityUUID;
			
			setWidth(160);
			setHeight(Math.ceil(slots / SLOTS_PER_ROW) * 40 + NAME_SIZE_SPACING);
		}
		
		private void render() {
			final GraphicsContext gc = getGraphicsContext2D();
			gc.clearRect(0, 0, getWidth(), getHeight());
			SimpleRenderer.renderInventory(gc, name, entity);
		}
	}
}
