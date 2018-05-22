package com.github.schnupperstudium.robots.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.LocalRobotsServer;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public final class LocalRobotsClient extends RobotsClient {
	private static final Logger LOG = LogManager.getLogger();
	
	private LocalRobotsClient() {
		super();
	}
	
	public static LocalRobotsClient connect(LocalRobotsServer server) {
		LocalRobotsClient robotsClient = new LocalRobotsClient();
		robotsClient.serverInterface = server.createServerInterface(robotsClient.createClientInterface()); 
		
		return robotsClient;
	}
	
	public RobotsClientInterface createClientInterface() {
		return new ClientInterface();
	}

	private class ClientInterface implements RobotsClientInterface {

		private ClientInterface() {

		}
		
		@Override
		public void updateEntity(long uuid, Entity entity) {
			try {
				LocalRobotsClient.this.updateEntity(uuid, entity != null ? entity.clone() : entity);
			} catch (CloneNotSupportedException e) {
				LOG.warn("could not clone entity: " + entity.getClass().getName());
				LocalRobotsClient.this.updateEntity(uuid, entity);
			}
		}

		@Override
		public void updateVisableTiles(long uuid, List<Tile> tiles) {
			List<Tile> tilesClone = new ArrayList<>(tiles.size());
			try {
				for (Tile t : tiles) {
					tilesClone.add(t.clone());
				}
			} catch (CloneNotSupportedException e) {
				LOG.warn("could not clone tiles");
				tilesClone = tiles;
			}
			
			LocalRobotsClient.this.updateVision(uuid, tilesClone);
		}

		@Override
		public EntityAction makeTurn(long uuid) {
			return LocalRobotsClient.this.makeTurn(uuid);
		}

		@Override
		public void updateWorld(long uuid, World world) {
			try {
				LocalRobotsClient.this.updateObserver(uuid, world != null ? world.clone() : world);
			} catch (CloneNotSupportedException e) {
				LOG.warn("could not clone world: " + world.getClass().getName());
				LocalRobotsClient.this.updateObserver(uuid, world);
			}
		}
	}
}
