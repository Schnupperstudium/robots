package com.github.schnupperstudium.robots.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ObserverViewController {
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
//		final Paint oldStorke = gc.getStroke();
		
		gc.clearRect(0, 0, width, height);
//		gc.setLineWidth(4);
//		gc.setStroke(Color.BLACK);
//		gc.strokeRect(0, 0, width, height);
//		
//		gc.setStroke(oldStorke);
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
}
