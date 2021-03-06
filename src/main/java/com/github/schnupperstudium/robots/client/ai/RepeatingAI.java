package com.github.schnupperstudium.robots.client.ai;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.MoveForwardAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.ai.action.TurnRightAction;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.RobotsClient;

public class RepeatingAI extends AbstractAI {
	private static final EntityAction[] DEFAULT_ACTIONS = new EntityAction[] {
			NoAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			NoAction.INSTANCE,
			TurnRightAction.INSTANCE,
			NoAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			NoAction.INSTANCE,
			TurnRightAction.INSTANCE,
			NoAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			NoAction.INSTANCE,
			TurnRightAction.INSTANCE,
			NoAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			NoAction.INSTANCE,
			TurnRightAction.INSTANCE,
	};
	
	private final EntityAction[] actions;

	private int turn;
	
	public RepeatingAI(RobotsClient client, long gameId, long uuid) {
		this(client, gameId, uuid, DEFAULT_ACTIONS);
	}
	
	public RepeatingAI(RobotsClient client, long gameId, long uuid, EntityAction... actions) {
		super(client, gameId, uuid);
		
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
