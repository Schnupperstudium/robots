package com.github.schnupperstudium.robots.client;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.network.KryoRegistry;
import com.github.schnupperstudium.robots.server.NetworkRobotsServer;
import com.github.schnupperstudium.robots.server.RobotsServerInterface;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public final class NetworkRobotsClient extends RobotsClient {
	private static final Logger LOG = LogManager.getLogger();
	private static final int DEFAULT_TIMEOUT = 10000;
	private final ObjectSpace objectSpace;
	private final Client client;
	
	private NetworkRobotsClient() {
		super();
		
		client = new Client(NetworkRobotsServer.WRITE_BUFFER_SIZE, NetworkRobotsServer.OBJECT_BUFFER_SIZE);
		KryoRegistry.registerClasses(client.getKryo());
		objectSpace = new ObjectSpace(client);
	}
	
	public static NetworkRobotsClient connect(String host) throws IOException {
		return connect(host, NetworkRobotsServer.DEFAULT_PORT);
	}
	
	public static NetworkRobotsClient connect(String host, int port) throws IOException {
		NetworkRobotsClient robotsClient = new NetworkRobotsClient();
		robotsClient.objectSpace.register(RobotsClientInterface.NETWORK_ID, robotsClient.createClientInterface());
		robotsClient.client.addListener(new Listener());
		robotsClient.client.start();
		robotsClient.client.connect(DEFAULT_TIMEOUT, host, port);
		
		robotsClient.serverInterface = ObjectSpace
				.getRemoteObject(robotsClient.client, RobotsServerInterface.NETWORK_ID, RobotsServerInterface.class);
		
		LOG.info("connected to {}:{}", host, port);
		return robotsClient;
	}
	
	public RobotsClientInterface createClientInterface() {
		return new ClientInterface();
	}

	@Override
	public void close() throws IOException {
		client.close();
		
		super.close();
	}
	
	private class ClientInterface implements RobotsClientInterface {

		private ClientInterface() {

		}
		
		@Override
		public void updateEntity(long uuid, Entity entity) {
			NetworkRobotsClient.this.updateEntity(uuid, entity);
		}

		@Override
		public void updateVisableTiles(long uuid, List<Tile> tiles) {
			NetworkRobotsClient.this.updateVision(uuid, tiles);
		}

		@Override
		public EntityAction makeTurn(long uuid) {
			return NetworkRobotsClient.this.makeTurn(uuid);
		}

		@Override
		public void updateWorld(long uuid, World world) {
			NetworkRobotsClient.this.updateObserver(uuid, world);
		}
	}
}
