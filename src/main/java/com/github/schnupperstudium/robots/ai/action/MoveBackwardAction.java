package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public class MoveBackwardAction extends EntityAction {
	public static final MoveBackwardAction INSTANCE = new MoveBackwardAction();
	
	public MoveBackwardAction() {
		
	}

	@Override
	public boolean apply(Game manager, Entity e) {
		return manager.moveEntity(e, e.getFacing().opposite());
	}
}
