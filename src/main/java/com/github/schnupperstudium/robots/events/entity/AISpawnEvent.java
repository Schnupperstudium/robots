package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.AI;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.World;

public class AISpawnEvent extends RobotSpawnEvent {
	private final AI ai;
	
	public AISpawnEvent(Game game, World world, Robot robot, RobotsClientInterface clientInterface) {
		super(world, robot);
		
		this.ai = new AI(game, clientInterface, robot);
	}
	
	public AI getAi() {
		return ai;
	}
	
	@Override
	public boolean apply(Game game) {
		boolean res = super.apply(game);		
		if (res) {
			game.addTickable(ai);
		}
		
		return res;
	}

}
