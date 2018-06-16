package com.github.schnupperstudium.robots.events.entity;

import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.server.AI;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.World;

public class AIDespawnEvent extends LivingEntityDespawnEvent {
	protected final AI ai;
	
	public AIDespawnEvent(World world, LivingEntity entity, AI ai) {
		super(world, entity);
		
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
