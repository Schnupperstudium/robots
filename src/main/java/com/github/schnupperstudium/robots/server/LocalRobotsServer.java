package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.List;

import com.github.schnupperstudium.robots.client.RobotsClientInterface;

public class LocalRobotsServer extends RobotsServer {

	public LocalRobotsServer() throws IOException {
		super();
	}
	
	@Override
	public RobotsServerInterface createServerInterface(RobotsClientInterface clientInterface) {
		return new RobotsServerInterface() {
			@Override
			public long startGame(String name, String levelName, String auth) {
				return LocalRobotsServer.this.startGame(name, levelName, auth);
			}
			
			@Override
			public long spawnEntity(long gameId, String name, String auth) {
				return LocalRobotsServer.this.spawnAI(gameId, name, auth, clientInterface);
			}
			
			@Override
			public boolean observerWorld(long gameId, String auth) {
				return LocalRobotsServer.this.observerWorld(gameId, auth, clientInterface);
			}
			
			@Override
			public List<Level> listLevels() {
				return LocalRobotsServer.this.listLevels();
			}
			
			@Override
			public List<GameInfo> listGames() {
				return LocalRobotsServer.this.listGames();
			}
		};
	}
}
