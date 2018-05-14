package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;

public class NetworkRobotsServer extends RobotsServer {
	private final Map<Connection, RobotsClientInterface> connectedClients = new HashMap<>();
	private final Server server;
	private final int port;	
	
	public NetworkRobotsServer(int port) throws IOException {
		super();
		
		this.port = port;
		this.server = new Server();
		
		server.addListener(new ServerListener());
		server.bind(port);
		server.start();
	}

	public int getPort() {
		return port;
	}
	
	private class ServerListener extends Listener {
		@Override
		public void connected(Connection connection) {
			final ObjectSpace objectSpace = new ObjectSpace(connection);
			final RobotsClientInterface clientInterface = ObjectSpace.getRemoteObject(connection, RobotsClientInterface.NETWORK_ID, RobotsClientInterface.class);
			objectSpace.register(RobotsServerInterface.NETWORK_ID, createServerInterface(clientInterface));
			connectedClients.put(connection, clientInterface);
		}
		
		@Override
		public void disconnected(Connection connection) {
			connectedClients.remove(connection);
		}
	}

	@Override
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
