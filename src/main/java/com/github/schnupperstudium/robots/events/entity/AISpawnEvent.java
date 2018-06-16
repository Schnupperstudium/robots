package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.server.AI;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.World;

public class AISpawnEvent extends LivingEntitySpawnEvent {
	private final AI ai;
	
	public AISpawnEvent(Game game, World world, LivingEntity entity, RobotsClientInterface clientInterface) {
		super(world, entity);
		
		this.ai = new AI(game, clientInterface, entity);
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
