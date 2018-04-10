package com.github.schnupperstudium.robots.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class RobotsServer implements Runnable {
	public static final int DEFAULT_PORT = 15681;
	
	private final List<String> levels = new ArrayList<>();
	private final Server server;
	private final int port;	

	private boolean run = true;
	
	public RobotsServer() throws IOException {
		this(DEFAULT_PORT);
	}
	
	public RobotsServer(int port) throws IOException {
		this.port = port;
		this.server = new Server();
		
		server.addListener(new ServerListener());
		server.bind(port);
		server.start();
		
		// scan for levels
		levels.add("<level>/<auth>");
	}
	
	@Override
	public void run() {
		while (run) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public int getPort() {
		return port;
	}
	
	private class ServerListener extends Listener {
		@Override
		public void connected(Connection connection) {
			final ObjectSpace space = new ObjectSpace(connection);
			space.register(RobotsServerInterface.NETWORK_ID, new RobotsServerInterface() {

				@Override
				public long joinGame(String name, String level, String auth) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public List<GameInfo> listRunningLevels() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public List<Level> listAvailableLevels() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public long startGame(String level, String auth) {
					// TODO Auto-generated method stub
					return 0;
				}			
				
			});
		}
		
		@Override
		public void disconnected(Connection connection) {
			
		}
	}
}
