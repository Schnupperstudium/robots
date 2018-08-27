package com.github.schnupperstudium.robots.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.world.Location;
import com.github.schnupperstudium.robots.world.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public final class MapOverlays {
	private MapOverlays() {
		
	}
	
	public static MapLocationOverlay createMapLocationOverlay(List<Location> locations, Paint paint, double alpha) {
		return new MapLocationOverlay(locations, paint, alpha);
	}
	
	public static class MapLocationOverlay implements MapRenderAddition {
		private final List<Location> locations;
		private final double alpha;
		private final Paint paint;
		
		public MapLocationOverlay(List<Location> locations, Paint paint, double alpha) {
			// list is not copied to allow live updating!
			this.locations = locations;
			this.paint = paint;
			this.alpha = alpha;
		}
		
		@Override
		public void renderMapAddition(Map map, GraphicsContext gc, int renderOffsetX, int renderOffsetY, double tileSize) {
			final double oldAlpha = gc.getGlobalAlpha();
			final Paint oldPaint = gc.getFill();
			gc.setGlobalAlpha(alpha);
			gc.setFill(paint);
			List<Location> locationsCopy = new ArrayList<>(locations);
			for (Location location : locationsCopy) {
				double renderX = (location.getX() - renderOffsetX) * tileSize;
				double renderY = (location.getY() - renderOffsetY) * tileSize;
				gc.fillRect(renderX, renderY, tileSize, tileSize);
			}
			
			gc.setGlobalAlpha(oldAlpha);
			gc.setFill(oldPaint);
		}
	}
}
