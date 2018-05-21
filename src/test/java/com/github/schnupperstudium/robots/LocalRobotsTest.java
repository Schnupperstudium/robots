package com.github.schnupperstudium.robots;

import java.io.IOException;

import com.github.schnupperstudium.robots.client.LocalRobotsClient;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.server.LocalRobotsServer;
import com.github.schnupperstudium.robots.server.RobotsServer;

public final class LocalRobotsTest extends RobotsTest {

	@Override
	protected RobotsServer setupServer() throws IOException {
		return new LocalRobotsServer();
	}

	@Override
	protected RobotsClient setupClient() throws IOException {
		return LocalRobotsClient.connect((LocalRobotsServer) robotsServer);
	}
}
