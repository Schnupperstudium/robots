package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public abstract class EntityAction {
	public abstract boolean apply(Game game, Entity entity);
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
