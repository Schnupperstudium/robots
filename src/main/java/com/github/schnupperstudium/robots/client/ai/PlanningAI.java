package com.github.schnupperstudium.robots.client.ai;

import java.util.LinkedList;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.entity.Item;

public abstract class PlanningAI extends AbstractAI {
	protected final LinkedList<EntityAction> stack = new LinkedList<>();
	
	public PlanningAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	@Override
	public EntityAction makeTurn() {
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
	protected void clearStack() {
		stack.clear();
	}
	
	/**
	 * @param action action to be queued.
	 */
	protected void enqueueAction(EntityAction... actions) {
		if (actions != null) {
			for (EntityAction action : actions) {
				stack.add(action);
			}
		}
	}
	
	protected void preAction() { };
	
	protected abstract void planMovement();
	
	/**
	 * Adds a turn left operation at the end of the queue.
	 */
	public void turnLeft() {
		enqueueAction(EntityAction.turnLeft());
	}
	
	/**
	 * Adds a turn right operation at the end of the queue.
	 */
	public void turnRight() {
		enqueueAction(EntityAction.turnRight());
	}
	
	/**
	 * Adds a move forward operation at the end of the queue.
	 */
	public void moveForward() {
		enqueueAction(EntityAction.moveForward());
	}
	
	/**
	 * Adds a move backward operations at the end of the queue.
	 */
	public void moveBackward() {
		enqueueAction(EntityAction.moveBackward());
	}
	
	/**
	 * Adds a turn left and a move forward operation to the end of the queue.
	 * Effectively turning the robot to the left and driving one tile forward.
	 */
	public void moveLeft() {
		enqueueAction(EntityAction.turnLeft(), EntityAction.moveForward());
	}
	
	/**
	 * Adds a turn right and a move forward operation to the end of the queue.
	 * Effectively turning the robot to the right and driving one tile forward.
	 */
	public void moveRight() {
		enqueueAction(EntityAction.turnRight(), EntityAction.moveForward());
	}

	/**
	 * Adds a pick up item operation at the end of the queue.
	 */
	public void pickUpItem() {
		enqueueAction(EntityAction.pickUpItem());
	}
	
	/**
	 * Adds a drop item operation at the end of the queue dropping the given item.
	 * If the given item is null this will result in a {@link EntityAction#noAction()}
	 * 
	 * @param item item to be dropped.
	 */
	public void dropItem(Item item) {
		enqueueAction(EntityAction.dropItem(item));
	}
	
	/**
	 * Adds a drop item operation at the end of the queue.
	 * 
	 * @param uuid uuid of the item to be dropped.
	 */
	public void dropItem(long uuid) {
		enqueueAction(EntityAction.dropItem(uuid));
	}
	
	/**
	 * Queues a pause operation at the end of the queue.
	 */
	public void noAction() {
		enqueueAction(EntityAction.noAction());
	}
}
