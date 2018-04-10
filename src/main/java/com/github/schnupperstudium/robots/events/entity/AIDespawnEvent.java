package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.server.AI;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.World;

public class AIDespawnEvent extends RobotDespawnEvent {
	protected final AI ai;
	
	public AIDespawnEvent(World world, Robot robot, AI ai) {
		super(world, robot);
		
		this.ai = ai;
	}
	
	public AI getAi() {
		return ai;
	}
	
	@Override
	public boolean apply(Game game) {
		boolean res = super.apply(game);		
		if (res) {
			game.removeTickable(ai);
		}
		
		return res;
	}

}
