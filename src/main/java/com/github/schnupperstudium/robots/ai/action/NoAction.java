package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public final class NoAction extends EntityAction {
	public static final NoAction INSTANCE = new NoAction();
	
	public NoAction() {

	}
	
	@Override
	public void apply(Game manager, Entity entity) {
		// yeah. So much to do here.
	}

}
