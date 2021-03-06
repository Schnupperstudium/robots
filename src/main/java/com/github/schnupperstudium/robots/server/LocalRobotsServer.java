package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.List;

import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;

public class LocalRobotsServer extends RobotsServer {

	public LocalRobotsServer() throws IOException {
		super();
	}
	
	public RobotsServerInterface createServerInterface(RobotsClientInterface clientInterface) {
		return new ServerInterface(clientInterface);
	}
	
	public class ServerInterface implements RobotsServerInterface {
		private final long connectionId = UUIDGenerator.obtain();
		private final RobotsClientInterface clientInterface;
		
		private ServerInterface(RobotsClientInterface clientInterface) {
			this.clientInterface = clientInterface;
		}
		
		@Override
		public long startGame(String name, String levelName, String auth) {
			return LocalRobotsServer.this.startGame(connectionId, name, levelName, auth, clientInterface);
		}
		
		@Override
		public long spawnEntity(long gameId, String name, String auth) {
			return LocalRobotsServer.this.spawnAI(connectionId, gameId, name, auth, clientInterface);
		}
		
		@Override
		public long spawnEntity(long gameId, String name, String auth, String entityType) {
			return LocalRobotsServer.this.spawnAI(connectionId, gameId, name, auth, clientInterface, entityType);
		}

		@Override
		public boolean observerWorld(long gameId, String auth) {
			return LocalRobotsServer.this.observeWorld(connectionId, gameId, auth, clientInterface);
		}
		
		@Override
		public List<Level> listLevels() {
			return LocalRobotsServer.this.listLevels();
		}
		
		@Override
		public List<GameInfo> listGames() {
			return LocalRobotsServer.this.listGames();
		}

		@Override
		public boolean removeEntity(long gameId, long entityUUID) {
			return LocalRobotsServer.this.despawnAI(connectionId, gameId, entityUUID, clientInterface);
		}

		@Override
		public boolean stopObserving(long gameId) {
			return LocalRobotsServer.this.unobserveWorld(connectionId, gameId, clientInterface);
		}
		
		public long getConnectionId() {
			return connectionId;
		}
	}
}
