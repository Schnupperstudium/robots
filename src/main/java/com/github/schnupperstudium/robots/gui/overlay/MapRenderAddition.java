package com.github.schnupperstudium.robots.gui.overlay;

import com.github.schnupperstudium.robots.world.Map;

import javafx.scene.canvas.GraphicsContext;

public interface MapRenderAddition {
	void renderMapAddition(Map map, GraphicsContext gc, int renderOffsetX, int renderOffsetY, double tileSize);
}
