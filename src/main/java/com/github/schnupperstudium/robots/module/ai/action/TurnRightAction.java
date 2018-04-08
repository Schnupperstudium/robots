package com.github.schnupperstudium.robots.module.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.GameManager;

public class TurnRightAction extends EntityAction {

	public TurnRightAction() {
		
	}

	@Override
	public void apply(GameManager manager, Entity e) {
		if (e instanceof Robot) {
			Robot robot = (Robot) e;
			robot.setFacing(robot.getFacing().right());
		}
	}
}
