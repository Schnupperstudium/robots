package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.network.KryoRegistry;

public class NetworkRobotsServer extends RobotsServer {
	public static final int DEFAULT_PORT = 15681;
	/** 8 MiB of read write buffer. */
	public static final int WRITE_BUFFER_SIZE = 8 * 1024 * 1024;
	/** 1 MiB for object writing. */
	public static final int OBJECT_BUFFER_SIZE = 1024 * 1024;

	private static final Logger LOG = LogManager.getLogger();
	
	private final Map<Connection, ServerInterface> connectedClients = new HashMap<>();
	private final Server server;
	private final int port;

	public NetworkRobotsServer() throws IOException {
		this(DEFAULT_PORT);
	}
	
	public NetworkRobotsServer(int port) throws IOException {
		super();
		
		this.port = port;
		this.server = new Server(WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE);
		KryoRegistry.registerClasses(server.getKryo());
		server.addListener(new ServerListener());
		server.bind(port);
		server.start();
		LOG.info("bound server to port {}", port);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		NetworkRobotsServer server = new NetworkRobotsServer();
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		while (line != null && !line.equalsIgnoreCase("exit")) {
			line = scanner.nextLine();
		}
		scanner.close();
		server.close();
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public void close() throws IOException {
		server.close();
		server.stop();
		
		super.close();
	}
	
	private class ServerListener extends Listener {
		@Override
		public void connected(Connection connection) {			
			final ObjectSpace objectSpace = new ObjectSpace(connection);
			final RobotsClientInterface clientInterface = ObjectSpace.getRemoteObject(connection, RobotsClientInterface.NETWORK_ID, RobotsClientInterface.class);
			final ServerInterface serverInterface = new ServerInterface(clientInterface);
			objectSpace.register(RobotsServerInterface.NETWORK_ID, serverInterface);
			connectedClients.put(connection, serverInterface);
			onConnect(serverInterface.connectionId);
		}
		
		@Override
		public void disconnected(Connection connection) {
			ServerInterface serverInterface = connectedClients.remove(connection);
			if (serverInterface != null) {
				onDisconnect(serverInterface.connectionId);
			}
		}
	}

	private class ServerInterface implements RobotsServerInterface {
		private final long connectionId = UUIDGenerator.obtain();
		private final RobotsClientInterface clientInterface;
		
		private ServerInterface(RobotsClientInterface clientInterface) {
			this.clientInterface = clientInterface;
		}
		
		@Override
		public long startGame(String name, String levelName, String auth) {
			return NetworkRobotsServer.this.startGame(connectionId, name, levelName, auth, clientInterface);
		}
		
		@Override
		public long spawnEntity(long gameId, String name, String auth) {
			return spawnEntity(gameId, name, auth, Robot.class.getName());
		}
		
		@Override
		public long spawnEntity(long gameId, String name, String auth, String entityType) {
			final long uuid = NetworkRobotsServer.this.spawnAI(connectionId, gameId, name, auth, clientInterface, entityType);
			
			return uuid;
		}

		@Override
		public boolean removeEntity(long gameId, long entityUUID) {
			final boolean removed = NetworkRobotsServer.this.despawnAI(connectionId, gameId, entityUUID, clientInterface);
						
			return removed;
		}

		@Override
		public boolean observerWorld(long gameId, String auth) {
			final boolean result = NetworkRobotsServer.this.observeWorld(connectionId, gameId, auth, clientInterface);
						
			return result;
		}
		
		@Override
		public boolean stopObserving(long gameId) {
			final boolean result = NetworkRobotsServer.this.unobserveWorld(connectionId, gameId, clientInterface);
						
			return result;
		}

		@Override
		public List<Level> listLevels() {
			return NetworkRobotsServer.this.listLevels();
		}
		
		@Override
		public List<GameInfo> listGames() {
			return NetworkRobotsServer.this.listGames();
		}
	}
}
