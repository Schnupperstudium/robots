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
import com.github.schnupperstudium.robots.client.RobotsClient;

public class RandomAI2 extends AbstractAI {
	
	private boolean moveForward = false;
	private int partyCounter = 0;
	
	public RandomAI2(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EntityAction makeTurn() {
		if (getBeneathTile().hasItem()) {
			partyCounter = 20;
			return PickUpItemAction.INSTANCE;
		}
		if (partyCounter > 0) {
			partyCounter--;
			return TurnRightAction.INSTANCE;
		}
		int randMax = (getFrontTile().canVisit()?4:0) + (getLeftTile().canVisit()?3:0) + (getRightTile().canVisit()?3:0);
		if (moveForward && getFrontTile().canVisit()) {
			//last move was a direction change. Now moving forward if possible
			moveForward = false;
			return MoveForwardAction.INSTANCE;
		} else if (randMax > 0) {
			int rand = (int)(Math.random()*randMax);
			if (getFrontTile().canVisit()) {
				if (rand < 4) {
					return MoveForwardAction.INSTANCE;
				} else {
					rand -= 4;
				}
			}
			if (getLeftTile().canVisit()) {
				if (rand < 3) {
					moveForward = true;
					return TurnLeftAction.INSTANCE;
				} else {
					rand -= 3;
				}
			}
			if (getRightTile().canVisit()) {
				moveForward = true;
				if (rand < 3) {
					return TurnRightAction.INSTANCE;
				}
			}
		}
		//front left and right occupied. Make random turn
		if (Math.random() < 0.5) {
			return TurnLeftAction.INSTANCE;
		} else {
			return TurnRightAction.INSTANCE;
		}
	}

}