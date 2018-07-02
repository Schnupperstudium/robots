package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;

public abstract class EntityAction {
	public abstract boolean apply(Game game, Entity entity);
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public static EntityAction noAction() {
		return NoAction.INSTANCE;
	}
	
	public static EntityAction dropItem(Item item) {
		if (item == null)
			return noAction();
		
		return dropItem(item.getUUID());
	}
	
	public static EntityAction dropItem(long uuid) {
		return new DropItemAction(uuid);
	}
	
	public static EntityAction pickUpItem() {
		return PickUpItemAction.INSTANCE;
	}
	
	public static EntityAction useItem(Item item) {
		if (item == null)
			return noAction();
		
		return useItem(item.getUUID());
	}
	
	public static EntityAction useItem(long uuid) {
		return new UseItemAction(uuid);
	}
	
	public static EntityAction moveForward() {
		return MoveForwardAction.INSTANCE;
	}
	
	public static EntityAction moveBackward() {
		return MoveBackwardAction.INSTANCE;
	}
	
	public static EntityAction turnLeft() {
		return TurnLeftAction.INSTANCE;
	}
	
	public static EntityAction turnRight() {
		return TurnRightAction.INSTANCE;
	}
}
