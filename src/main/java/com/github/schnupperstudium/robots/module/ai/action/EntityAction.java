package com.github.schnupperstudium.robots.module.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.GameManager;

public abstract class EntityAction {
	public abstract void apply(GameManager manager, Entity entity);
}
