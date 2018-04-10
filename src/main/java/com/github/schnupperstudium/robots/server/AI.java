package com.github.schnupperstudium.robots.server;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class AI implements Tickable {
	private static final int ROBOT_VISION = 3;
	
	private final RobotsClientInterface client;		
	private final Robot robot;
	
	public AI(RobotsClientInterface client, Robot robot) {
		this.client = client;
		this.robot = robot;
	}
	
	@Override
	public void update(Game game) {
		updateEntity();
		updateVision(game);
		EntityAction action = makeTurn();
		action.apply(game, robot);
	}

	public EntityAction makeTurn() {
		if (robot.isAlive()) {
			return client.makeTurn(robot.getUUID());
		} else {
			return NoAction.INSTANCE;
		}
	}
	
	public void updateEntity() {
		client.updateEntity(robot.getUUID(), robot);
	}
	
	public void updateVision(Game game) {
		if (robot.isDead())
			return;
		
		final World world = game.getWorld();
		final List<Tile> visibleTiles = new ArrayList<>((ROBOT_VISION + 1) * (ROBOT_VISION + 1));
		final int robotX = robot.getX();
		final int robotY = robot.getY();
		for (int x = robotX - ROBOT_VISION; x < robotX + ROBOT_VISION; x++) {
			if (x < 0 || x >= world.getWidth())
				continue;
			
			for (int y = robotY - ROBOT_VISION; y < robotY + ROBOT_VISION; y++) {
				if (y < 0 || y >= world.getHeight())
					continue;
				
				visibleTiles.add(world.getTile(x, y));
			}
		}
		
		client.updateVisableTiles(robot.getUUID(), visibleTiles);
	}
	
	public RobotsClientInterface getClient() {
		return client;
	}
	
	public Robot getRobot() {
		return robot;
	}
}
