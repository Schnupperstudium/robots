package com.github.schnupperstudium.robots.module.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.GameManager;

public class MoveBackwardAction extends EntityAction {

	public MoveBackwardAction() {
		
	}

	@Override
	public void apply(GameManager manager, Entity e) {
		if (e instanceof Robot) {
			Robot robot = (Robot) e;
			manager.moveEntity(e, robot.getFacing().opposite());
		}
	}
}