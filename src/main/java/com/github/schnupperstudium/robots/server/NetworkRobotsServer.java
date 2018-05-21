package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.network.KryoRegistry;

public class NetworkRobotsServer extends RobotsServer {
	public static final int DEFAULT_PORT = 15681;	

	private static final Logger LOG = LogManager.getLogger();
	
	private final Map<Connection, RobotsClientInterface> connectedClients = new HashMap<>();
	private final Server server;
	private final int port;

	public NetworkRobotsServer() throws IOException {
		this(DEFAULT_PORT);
	}
	
	public NetworkRobotsServer(int port) throws IOException {
		super();
		
		this.port = port;
		this.server = new Server();
		
		KryoRegistry.registerClasses(server.getKryo());
		server.addListener(new ServerListener());
		server.bind(port);
		server.start();
		LOG.info("bound server to port {}", port);
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public void close() throws IOException {
		server.close();
		
		super.close();
	}
	
	private class ServerListener extends Listener {
		@Override
		public void connected(Connection connection) {
			final ObjectSpace objectSpace = new ObjectSpace(connection);
			final RobotsClientInterface clientInterface = ObjectSpace.getRemoteObject(connection, RobotsClientInterface.NETWORK_ID, RobotsClientInterface.class);
			objectSpace.register(RobotsServerInterface.NETWORK_ID, createServerInterface(clientInterface));
			connectedClients.put(connection, clientInterface);
			LOG.info("client disconnected '{}'", connection.getRemoteAddressTCP().getAddress());
		}
		
		@Override
		public void disconnected(Connection connection) {
			LOG.info("client disconnected '{}'", connection.getRemoteAddressTCP());
			connectedClients.remove(connection);
		}
	}

	public RobotsServerInterface createServerInterface(final RobotsClientInterface clientInterface) {
		return new RobotsServerInterface() {
			@Override
			public long startGame(String name, String levelName, String auth) {
				return NetworkRobotsServer.this.startGame(name, levelName, auth);
			}
			
			@Override
			public long spawnEntity(long gameId, String name, String auth) {
				return NetworkRobotsServer.this.spawnAI(gameId, name, auth, clientInterface);
			}
			
			@Override
			public boolean observerWorld(long gameId, String auth) {
				return NetworkRobotsServer.this.observerWorld(gameId, auth, clientInterface);
			}
			
			@Override
			public List<Level> listLevels() {
				return NetworkRobotsServer.this.listLevels();
			}
			
			@Override
			public List<GameInfo> listGames() {
				return NetworkRobotsServer.this.listGames();
			}
		};
	}
}
