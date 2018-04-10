package com.github.schnupperstudium.robots.client;

import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public interface RobotsClientInterface {
	void updateEntity(long uuid, Entity entity);
	void updateVisableTiles(long uuid, List<Tile> tiles);
	EntityAction makeTurn(long uuid);
	void updateWorld(long uuid, World world);
}
