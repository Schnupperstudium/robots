package com.github.schnupperstudium.robots;

import java.io.IOException;

import com.github.schnupperstudium.robots.client.NetworkRobotsClient;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.server.NetworkRobotsServer;
import com.github.schnupperstudium.robots.server.RobotsServer;

public final class NetworkRobotsTest extends RobotsTest {

	@Override
	protected RobotsServer setupServer() throws IOException {
		return new NetworkRobotsServer();
	}

	@Override
	protected RobotsClient setupClient() throws IOException {		
		return NetworkRobotsClient.connect("127.0.0.1");
	}
}
