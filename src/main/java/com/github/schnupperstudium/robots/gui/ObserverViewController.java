package com.github.schnupperstudium.robots.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;

public class ObserverViewController {
	@FXML 
	private AnchorPane worldCanvasAnchor;
		
	protected Canvas worldCanvas;
	
	public ObserverViewController() {
		
	}
	
	@FXML
	public void initialize() {
		worldCanvas = new ResizableCanvas();
		worldCanvasAnchor.getChildren().add(worldCanvas);
	}

	protected void clearCanvas() {
		worldCanvas.getGraphicsContext2D().clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
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
