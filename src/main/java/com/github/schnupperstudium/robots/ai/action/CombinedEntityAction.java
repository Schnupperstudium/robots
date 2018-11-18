package com.github.schnupperstudium.robots.ai.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;

public class CombinedEntityAction extends EntityAction {
	private final List<EntityAction> actions;
	
	public CombinedEntityAction(EntityAction... actions) {		
		this.actions = Arrays.asList(actions);
	}
	
	public CombinedEntityAction(Collection<? extends EntityAction> actions) {
		if (actions == null)
			this.actions = new ArrayList<>();
		else
			this.actions = new ArrayList<>(actions);		
	}
	
	@Override
	public boolean apply(Game game, Entity entity) {
		for (EntityAction action : actions) {
			if (!action.apply(game, entity))
				return false;
		}
		
		return true;
	}
	
	public List<EntityAction> getActions() {
		return actions;
	}
}
