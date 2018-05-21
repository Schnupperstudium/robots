package com.github.schnupperstudium.robots.client;

import java.util.List;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.LocalRobotsServer;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public final class LocalRobotsClient extends RobotsClient {
	
	private LocalRobotsClient() {
		super();
	}
	
	public static LocalRobotsClient connect(LocalRobotsServer server) {
		LocalRobotsClient robotsClient = new LocalRobotsClient();
		robotsClient.serverInterface = server.createServerInterface(robotsClient.createClientInterface()); 
		
		return robotsClient;
	}
	
	@Override
	public RobotsClientInterface createClientInterface() {
		return new ClientInterface();
	}

	private class ClientInterface implements RobotsClientInterface {

		private ClientInterface() {

		}
		
		@Override
		public void updateEntity(long uuid, Entity entity) {
			LocalRobotsClient.this.updateEntity(uuid, entity);
		}

		@Override
		public void updateVisableTiles(long uuid, List<Tile> tiles) {
			LocalRobotsClient.this.updateVision(uuid, tiles);
		}

		@Override
		public EntityAction makeTurn(long uuid) {
			return LocalRobotsClient.this.makeTurn(uuid);
		}

		@Override
		public void updateWorld(long uuid, World world) {
			LocalRobotsClient.this.updateObserver(uuid, world);
		}
	}
}
