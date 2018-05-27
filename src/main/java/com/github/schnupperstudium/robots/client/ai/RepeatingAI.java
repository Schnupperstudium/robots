package com.github.schnupperstudium.robots.client.ai;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.client.AbstractAI;

public class RepeatingAI extends AbstractAI {
	private final EntityAction[] actions;

	private int turn;
	
	public RepeatingAI(long uuid, EntityAction... actions) {
		super(uuid);
		
		if (actions == null || actions.length == 0)
			this.actions = new EntityAction[] { NoAction.INSTANCE };
		else
			this.actions = actions;
		
		this.turn = 0;
	}
	
	@Override
	public EntityAction makeTurn() {
		return actions[turn++ % actions.length];
	}

}
