package com.github.schnupperstudium.robots.gui.overlay;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import com.github.schnupperstudium.robots.gui.overlay.tile.TileColorOverlay;
import com.github.schnupperstudium.robots.gui.overlay.tile.TileTextOverlay;
import com.github.schnupperstudium.robots.world.Map;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.scene.canvas.GraphicsContext;

public class MapRenderDecorator implements MapRenderAddition {
	private List<TileAddition> renderAdditions = new CopyOnWriteArrayList<>(); 
	
	public TileTextOverlay getTextOverlay(int x, int y) {
		return getRenderAddition(x, y, TileTextOverlay.class, TileTextOverlay::new);
	}

	public TileColorOverlay getColorOverlay(int x, int y) {
		return getRenderAddition(x, y, TileColorOverlay.class, TileColorOverlay::new);
	}
	
	@Override
	public void renderMapAddition(Map map, GraphicsContext gc, int renderOffsetX, int renderOffsetY, double tileSize) {
		for (TileAddition addition : renderAdditions) {
			final Tile tile = map.getTile(addition.x, addition.y);
			final double renderX = (addition.x - renderOffsetX) * tileSize;
			final double renderY = (addition.y - renderOffsetY) * tileSize;
			
			addition.renderTileAddition(tile, gc, renderX, renderY, tileSize);
		}
	}

	public <T extends TileRenderAddition> T getRenderAddition(int x, int y, Class<T> clazz) {
		for (TileAddition addition : renderAdditions) {
			if (addition.x != x || addition.y != y)
				continue;
			
			if (clazz.equals(addition.getContainedClass()))
				return clazz.cast(addition.renderAddition);
		}
		
		return null;
	}
	
	public <T extends TileRenderAddition> T getRenderAddition(int x, int y, Class<T> clazz, Supplier<T> factory) {
		T renderAddition = getRenderAddition(x, y, clazz);
		
		if (renderAddition == null) {
			renderAddition = factory.get();
			if (renderAddition != null) {
				renderAdditions.add(new TileAddition(x, y, renderAddition));
			}
		}
		
		return renderAddition;
	}
	
	private static class TileAddition implements TileRenderAddition {
		private final int x;
		private final int y;		
		private final TileRenderAddition renderAddition;
		
		private TileAddition(int x, int y, TileRenderAddition renderAddition) {
			this.x = x;
			this.y = y;
			this.renderAddition = renderAddition;
		}
		
		@Override
		public void renderTileAddition(Tile tile, GraphicsContext gc, double renderX, double renderY, double tileSize) {
			renderAddition.renderTileAddition(tile, gc, renderX, renderY, tileSize);
		}
		
		private Class<? extends TileRenderAddition> getContainedClass() {
			return renderAddition.getClass();
		}
	}
}
