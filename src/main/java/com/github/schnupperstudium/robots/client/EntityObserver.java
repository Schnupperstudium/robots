package com.github.schnupperstudium.robots.client;

import com.github.schnupperstudium.robots.entity.Entity;

public interface EntityObserver {
	void onEntityUpdate(AbstractAI ai, Entity entity);
}
