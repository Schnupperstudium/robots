package com.github.schnupperstudium.robots.gui.overlay.tile;

import com.github.schnupperstudium.robots.gui.overlay.TileRenderAddition;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class TileTextOverlay implements TileRenderAddition {
	private Paint paint;
	private String text;
	
	public TileTextOverlay() {
		this(Color.BLACK, "");
	}
	
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
		gc.fillText(text, renderX + 0.1 * tileSize, renderY + 0.75 * tileSize, 0.8 * tileSize);
		gc.setFill(oldPaint);
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}