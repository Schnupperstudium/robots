package com.github.schnupperstudium.robots.server;

import com.github.schnupperstudium.robots.entity.LivingEntity;

public interface LivingEntityFactory {
	LivingEntity create(Game game, int x, int y, String name);
}
