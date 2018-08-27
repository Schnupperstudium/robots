package com.github.schnupperstudium.robots.gui.overlay;

import com.github.schnupperstudium.robots.world.Tile;

import javafx.scene.canvas.GraphicsContext;

public interface TileRenderAddition {
	void renderTileAddition(Tile tile, GraphicsContext gc, double renderX, double renderY, double tileSize);
}
