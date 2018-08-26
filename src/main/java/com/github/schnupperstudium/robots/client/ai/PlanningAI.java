package com.github.schnupperstudium.robots.client.ai;

import java.util.LinkedList;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.RobotsClient;

public abstract class PlanningAI extends AbstractAI {
	protected final LinkedList<EntityAction> stack = new LinkedList<>();
	
	public PlanningAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	@Override
	public final EntityAction makeTurn() {
		if (stack.isEmpty())
			planMovement();
			
		// give implementing class a chance to do something
		preAction();
		
		if (stack.isEmpty())
			return NoAction.INSTANCE;
		
		// actually perform action
		final EntityAction action = stack.pop();		
		if (action != null)
			return action;
		else
			return NoAction.INSTANCE;
	}

	/**
	 * Clears currently planned actions.
	 */
	protected void clearPath() {
		stack.clear();
	}
	
	protected void enqueueAction(EntityAction action) {
		stack.add(action);
	}
	
	protected void preAction() { };
	
	protected abstract void planMovement();
}
