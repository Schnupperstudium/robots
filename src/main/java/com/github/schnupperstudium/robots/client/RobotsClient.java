package com.github.schnupperstudium.robots.client;

import java.util.Objects;

import com.github.schnupperstudium.robots.server.RobotsServerInterface;

public final class RobotsClient {
	private final RobotsServerInterface serverInterface;
	
	private RobotsClient(RobotsServerInterface serverInterface) {
		this.serverInterface = serverInterface;
	}
	
	public static RobotsClient connect(RobotsServerInterface serverInterface) {
		Objects.requireNonNull(serverInterface);
		
		return new RobotsClient(serverInterface);
	}
	
	public static RobotsClient connect(String host, int port) {
		// TODO: implement
		return null;
	}
	
	public RobotsServerInterface getServerInterface() {
		return serverInterface;
	}
	
	
}
