package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.Game;

public class TurnLeftAction extends EntityAction {
	public static final TurnLeftAction INSTANCE = new TurnLeftAction();
	
	public TurnLeftAction() {
		
	}

	@Override
	public void apply(Game manager, Entity e) {
		if (e instanceof Robot) {
			Robot robot = (Robot) e;
			robot.setFacing(robot.getFacing().left());
		}
	}
}
