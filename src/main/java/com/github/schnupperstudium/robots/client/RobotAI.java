package com.github.schnupperstudium.robots.client;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

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
	
	public Facing getFacing() {
		return getEntity().getFacing();
	}
	
	@Override
	public Robot getEntity() {		
		return (Robot) super.getEntity();
	}
}
