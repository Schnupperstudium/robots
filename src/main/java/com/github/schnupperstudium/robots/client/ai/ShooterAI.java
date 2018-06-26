package com.github.schnupperstudium.robots.client.ai;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.ai.action.PickUpItemAction;
import com.github.schnupperstudium.robots.ai.action.TurnLeftAction;
import com.github.schnupperstudium.robots.ai.action.TurnRightAction;
import com.github.schnupperstudium.robots.ai.action.UseItemAction;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.entity.Item;

public class ShooterAI extends AbstractAI {
	private int turn = 0;
	
	public ShooterAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	@Override
	public EntityAction makeTurn() {
		switch (turn++) {
		case 0:
			if (getRightTile().canVisit())
				return TurnRightAction.INSTANCE;
			else 
				return TurnLeftAction.INSTANCE;
		case 5:
			return PickUpItemAction.INSTANCE;
		case 20:
			if (getEntity().getInventory().getUsedSize() < 1)
				return NoAction.INSTANCE;
					
			Item item = getEntity().getInventory().getItems().get(0);
			return new UseItemAction(item.getUUID());
		default:
			return NoAction.INSTANCE;
		}
	}
}
