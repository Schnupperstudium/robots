package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public class TurnRightAction extends EntityAction {
	public static final TurnRightAction INSTANCE = new TurnRightAction();
	
	public TurnRightAction() {
		
	}

	@Override
	public boolean apply(Game manager, Entity e) {
		e.setFacing(e.getFacing().right());
		return true;
	}
}
