package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.world.World;

public class RobotDespawnEvent extends EntityDespawnEvent {
	protected final Robot robot;
	
	public RobotDespawnEvent(World world, Robot robot) {
		super(world, robot);
		
		this.robot = robot;
	}
}
