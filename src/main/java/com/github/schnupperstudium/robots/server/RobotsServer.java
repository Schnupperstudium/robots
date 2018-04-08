package com.github.schnupperstudium.robots.server;

public class RobotsServer {
	public static final int DEFAULT_PORT = 15681;
	
	private final int port;	
	
	public RobotsServer() {
		this(DEFAULT_PORT);
	}
	
	public RobotsServer(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
}
