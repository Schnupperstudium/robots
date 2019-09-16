package com.github.schnupperstudium.robots.gui.overlay.tile;

import com.github.schnupperstudium.robots.gui.overlay.TileRenderAddition;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class TileColorOverlay implements TileRenderAddition {
	private double alpha;
	private Paint paint;
	
	public TileColorOverlay() {
		this(Color.BLACK, 1);
	}
	
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
	
	public double getAlpha() {
		return alpha;
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
}