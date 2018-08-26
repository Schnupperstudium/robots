package com.github.schnupperstudium.robots.gui;

import com.github.schnupperstudium.robots.world.Tile;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public final class TileOverlays {
	private TileOverlays() {
		
	}
	
	public static TileColorOverlay createColorOverlay(Paint paint, double alpha) {
		return new TileColorOverlay(paint, alpha);
	}
	
	public static class TileColorOverlay implements TileRenderAddition {
		private final double alpha;
		private final Paint paint;
		
		public TileColorOverlay(Paint paint, double alpha) {
			this.paint = paint;
			this.alpha = alpha;
		}
		
		@Override
		public void renderTileAddition(Tile tile, GraphicsContext gc, double renderX, double renderY, double tileSize) {
			final double oldAlpha = gc.getGlobalAlpha();
			final Paint oldPaint = gc.getFill();
			gc.setGlobalAlpha(alpha);
			gc.setFill(paint);
			gc.fillRect(renderX, renderY, tileSize, tileSize);
			gc.setFill(oldPaint);
			gc.setGlobalAlpha(oldAlpha);
		}
	}
	
	public static class TileTextOverlay implements TileRenderAddition {
		private final Paint paint;
		private final String text;
		
		public TileTextOverlay(String text) {
			this(Color.BLACK, text);
		}
		
		public TileTextOverlay(Paint paint, String text) {
			this.paint = paint;
			this.text = text;
		}
		
		@Override
		public void renderTileAddition(Tile tile, GraphicsContext gc, double renderX, double renderY, double tileSize) {
			final Paint oldPaint = gc.getFill();
			gc.setFill(paint);
			gc.fillText(text, renderX, renderY, tileSize);
			gc.setFill(oldPaint);
		}
	}
}
