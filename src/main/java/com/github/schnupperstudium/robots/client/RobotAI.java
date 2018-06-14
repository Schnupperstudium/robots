package com.github.schnupperstudium.robots.client;

import com.github.schnupperstudium.robots.entity.Robot;

/**
 * An AI controlling a robot.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class RobotAI extends AbstractAI {

	public RobotAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}
	
	@Override
	public Robot getEntity() {		
		return (Robot) super.getEntity();
	}
}
