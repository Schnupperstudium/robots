package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public class MoveForwardAction extends EntityAction {
	public static final MoveForwardAction INSTANCE = new MoveForwardAction();
	
	public MoveForwardAction() {
		
	}

	@Override
	public boolean apply(Game manager, Entity e) {
		return manager.moveEntity(e, e.getFacing());
	}

}
