package com.github.schnupperstudium.robots.client;

import com.github.schnupperstudium.robots.world.World;

public interface IWorldObserver {
	void updateWorld(long gameId, World world);
}
