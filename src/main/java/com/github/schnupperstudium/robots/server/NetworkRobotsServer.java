package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
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
		while (true)
			Thread.sleep(100);
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
		}
		
		@Override
		public void disconnected(Connection connection) {
			ServerInterface serverInterface = connectedClients.remove(connection);
			if (serverInterface != null)
				serverInterface.onDisconnect();
		}
	}

	private class ServerInterface implements RobotsServerInterface {
		private final List<ObserverData> observerDatas;
		private final List<AIData> aiDatas;
		private final RobotsClientInterface clientInterface;
		
		private ServerInterface(RobotsClientInterface clientInterface) {
			this.observerDatas = new ArrayList<>();
			this.aiDatas = new ArrayList<>();
			this.clientInterface = clientInterface;
		}
		
		@Override
		public long startGame(String name, String levelName, String auth) {
			return NetworkRobotsServer.this.startGame(name, levelName, auth);
		}
		
		@Override
		public long spawnEntity(long gameId, String name, String auth) {
			final long uuid = NetworkRobotsServer.this.spawnAI(gameId, name, auth, clientInterface);
			if (UUIDGenerator.isValid(uuid)) {
				synchronized (aiDatas) {
					aiDatas.add(new AIData(gameId, uuid));
				}
			}
			
			return uuid;
		}
		
		@Override
		public boolean removeEntity(long gameId, long entityUUID) {
			final boolean removed = NetworkRobotsServer.this.despawnAI(gameId, entityUUID);
			if (removed) {
				synchronized (aiDatas) {
					Iterator<AIData> it = aiDatas.iterator();
					while (it.hasNext()) {
						if (it.next().match(gameId, entityUUID)) {
							it.remove();
							break;
						}
					}
				}
			}
			
			return removed;
		}

		@Override
		public boolean observerWorld(long gameId, String auth) {
			final boolean result = NetworkRobotsServer.this.observeWorld(gameId, auth, clientInterface);
			if (result) {
				synchronized (observerDatas) {
					observerDatas.add(new ObserverData(gameId));
				}
			}
			
			return result;
		}
		
		@Override
		public boolean stopObserving(long gameId) {
			final boolean result = NetworkRobotsServer.this.unobserveWorld(gameId, clientInterface);
			if (result) {
				synchronized (observerDatas) {
					Iterator<ObserverData> it = observerDatas.iterator();
					while (it.hasNext()) {
						if (it.next().match(gameId)) {
							it.remove();
							break;
						}
					}
				}
			}
			
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

		public void onDisconnect() {
			// remove any remaining AI
			synchronized (aiDatas) {
				for (AIData data : aiDatas) {
					NetworkRobotsServer.this.despawnAI(data.gId, data.eId);
				}
				
				aiDatas.clear();
			}
			
			// remove any remaining observer
			synchronized (observerDatas) {
				for (ObserverData data : observerDatas) {
					NetworkRobotsServer.this.unobserveWorld(data.gameId, clientInterface);
				}
				
				observerDatas.clear();
			}
		}
	}
	
	private static class ObserverData {
		private final long gameId;
		
		private ObserverData(long gameId) {
			this.gameId = gameId;
		}
		
		protected boolean match(long gId) {
			return this.gameId == gId;
		}
	}
	
	private static class AIData {
		private final long gId;
		private final long eId;
		
		private AIData(long gId, long eId) {
			this.gId = gId;
			this.eId = eId;
		}
		
		protected boolean match(long gId, long eId) {
			return this.gId == gId && this.eId == eId;
		}
	}
}
