package com.github.schnupperstudium.robots.client.ai;

import com.github.schnupperstudium.robots.client.AbstractRunningAI;
import com.github.schnupperstudium.robots.client.RobotsClient;

public class ExploringAI extends AbstractRunningAI {

	public ExploringAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	@Override
	protected void run() throws InterruptedException {
		while (true) {
			while (getFrontTile().canVisit())
				driveForward();
			
			if (getRightTile().canVisit())
				turnRight();
			else 
				turnLeft();
		}
	}

}
