package com.github.schnupperstudium.robots.client.ai;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.RobotsClient;

public class YourFirstAI extends AbstractAI {

	public YourFirstAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	@Override
	public EntityAction makeTurn() {
		if (getFrontTile().canVisit()) {
			return EntityAction.moveForward();
		} else {
			return EntityAction.turnLeft();
		}
	}
}
