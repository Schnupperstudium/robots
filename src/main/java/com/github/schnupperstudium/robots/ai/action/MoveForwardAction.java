package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.Game;

public class MoveForwardAction extends EntityAction {
	public static final MoveForwardAction INSTANCE = new MoveForwardAction();
	
	public MoveForwardAction() {
		
	}

	@Override
	public void apply(Game manager, Entity e) {
		if (e instanceof Robot) {
			Robot robot = (Robot) e;
			manager.moveEntity(e, robot.getFacing());
		}
	}

}
