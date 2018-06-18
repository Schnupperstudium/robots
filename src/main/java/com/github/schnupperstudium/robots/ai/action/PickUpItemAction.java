package com.github.schnupperstudium.robots.ai.action;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public class PickUpItemAction extends EntityAction {
	public static final PickUpItemAction INSTANCE = new PickUpItemAction();
	
	public PickUpItemAction() {
		
	}
	
	@Override
	public boolean apply(Game manager, Entity entity) {		
		return manager.pickUpItem(entity) != null;
	}
}
