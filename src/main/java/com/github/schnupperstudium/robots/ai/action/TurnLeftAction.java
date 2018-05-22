package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public class TurnLeftAction extends EntityAction {
	public static final TurnLeftAction INSTANCE = new TurnLeftAction();
	
	public TurnLeftAction() {
		
	}

	@Override
	public boolean apply(Game manager, Entity e) {
		e.setFacing(e.getFacing().left());
		return true;
	}
}
