package com.github.schnupperstudium.robots.client.ai;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.entity.projectile.LaserBeam;

public class ShooterAI extends AbstractAI {
	private int turn = 0;
	
	public ShooterAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	@Override
	public EntityAction makeTurn() {
		if (getBeneathTile().hasItem())
			return EntityAction.pickUpItem();
		
		if (!getFrontTile().canVisit() && !(getFrontTile().getVisitor() instanceof LaserBeam))
			return EntityAction.turnRight();
		
		if (getEntity().getInventory().isEmpty() && getFrontTile().canVisit())
			return EntityAction.moveForward();
		
		if (!getEntity().getInventory().isEmpty() && turn++ >= 10) {
			turn = 0;
			return EntityAction.useItem(getEntity().getInventory().getItems().get(0));
		} else {
			return EntityAction.noAction();
		}
	}
}
