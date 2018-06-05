package com.github.schnupperstudium.robots;

import java.io.IOException;

import com.github.schnupperstudium.robots.client.NetworkRobotsClient;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.client.ai.RandomAI;

public class NetworkRobotAI {
	public static void main(String[] args) throws IOException, InterruptedException {
		RobotsClient client = NetworkRobotsClient.connect("127.0.0.1");
		long gameId = client.getServerInterface().startGame("MyGame", "WaterPond", null);
		client.spawnAI(gameId, "RandomAI", null, (c, gId, eId) -> new RandomAI(c, gId, eId));
		
		while (true) {
			Thread.sleep(100);
		}
	}
}
