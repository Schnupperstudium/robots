package com.github.schnupperstudium.robots.client.ai;

import java.util.Random;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.MoveBackwardAction;
import com.github.schnupperstudium.robots.ai.action.MoveForwardAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.ai.action.PickUpItemAction;
import com.github.schnupperstudium.robots.ai.action.TurnLeftAction;
import com.github.schnupperstudium.robots.ai.action.TurnRightAction;
import com.github.schnupperstudium.robots.client.AbstractAI;

public class RandomAI extends AbstractAI {
	private static final EntityAction[] DEFAULT_ACTIONS = new EntityAction[] {
		TurnLeftAction.INSTANCE,
		TurnRightAction.INSTANCE,
		MoveForwardAction.INSTANCE,
		MoveBackwardAction.INSTANCE,
		PickUpItemAction.INSTANCE
	};
	
	private final Random random = new Random();
	private final EntityAction[] actions;
	
	public RandomAI(long uuid) {
		this(uuid, DEFAULT_ACTIONS);
	}
	
	public RandomAI(long uuid, EntityAction... actions) {
		super(uuid);
		
		if (actions == null || actions.length == 0)
			this.actions = new EntityAction[] { NoAction.INSTANCE };
		else
			this.actions = actions;
	}
	
	@Override
	public EntityAction makeTurn() {
		final int index = random.nextInt(actions.length);
		
		return actions[index];
	}

}
