package com.github.schnupperstudium.robots.client;

import java.util.List;

import com.github.schnupperstudium.robots.world.Tile;

public interface VisionObserver {
	void onVisionUpdate(AbstractAI ai, List<Tile> vision);
}
