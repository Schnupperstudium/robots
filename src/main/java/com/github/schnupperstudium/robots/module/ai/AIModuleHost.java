package com.github.schnupperstudium.robots.module.ai;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.module.ai.action.EntityAction;
import com.github.schnupperstudium.robots.server.GameManager;
import com.github.schnupperstudium.robots.server.Module;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class AIModuleHost implements Module, AIServerModule {
	public static final int ROBOT_VISION = 3;
	
	private final List<RemoteAI> clients = new ArrayList<>();
	private GameManager manager;
	
	public AIModuleHost() {
		
	}
	
	@Override
	public void updateModule() {
		synchronized (clients) {
			for (RemoteAI ai : clients) {
				ai.updateEntity();
				ai.updateVision();	
				EntityAction action = ai.makeTurn();
				action.apply(manager, ai.robot);
			}
		}		
	}

	@Override
	public long joinGame(String name) {
		return 0;
	}

	@Override
	public void load(GameManager gameManager) {
		this.manager = gameManager;
	}

	@Override
	public void unload() {
		this.manager = null;
	}
	
	private class RemoteAI {
		private final AIClientModule client;		
		private final Robot robot;
		
		private RemoteAI(AIClientModule client, Robot robot) {
			this.client = client;
			this.robot = robot;
		}
		
		private EntityAction makeTurn() {
			return client.makeTurn(robot.getUUID());
		}
		
		private void updateEntity() {
			client.updateEntity(robot.getUUID(), robot);
		}
		
		private void updateVision() {			
			final World world = manager.getWorld();
			final List<Tile> visibleFields = new ArrayList<>((ROBOT_VISION + 1) * (ROBOT_VISION + 1));
			final int robotX = robot.getX();
			final int robotY = robot.getY();
			for (int x = robotX - ROBOT_VISION; x < robotX + ROBOT_VISION; x++) {
				if (x < 0 || x >= world.getWidth())
					continue;
				
				for (int y = robotY - ROBOT_VISION; y < robotY + ROBOT_VISION; y++) {
					if (y < 0 || y >= world.getHeight())
						continue;
					
					visibleFields.add(world.getField(x, y));
				}
			}
			
			client.updateVisableFields(robot.getUUID(), visibleFields);
		}
	}
}
