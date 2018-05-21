package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.Game;

public class TurnRightAction extends EntityAction {
	public static final TurnRightAction INSTANCE = new TurnRightAction();
	
	public TurnRightAction() {
		
	}

	@Override
	public void apply(Game manager, Entity e) {
		if (e instanceof Robot) {
			Robot robot = (Robot) e;
			robot.setFacing(robot.getFacing().right());
		}
	}
}
