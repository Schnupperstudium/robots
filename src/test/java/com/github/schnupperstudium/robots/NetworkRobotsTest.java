package com.github.schnupperstudium.robots;

import java.io.IOException;

import org.junit.Test;

import com.github.schnupperstudium.robots.client.NetworkRobotsClient;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.NetworkRobotsServer;
import com.github.schnupperstudium.robots.server.RobotsServer;

import junit.framework.Assert;

public final class NetworkRobotsTest extends RobotsTest {

	@Override
	protected RobotsServer setupServer() throws IOException {
		return new NetworkRobotsServer();
	}

	@Override
	protected RobotsClient setupClient() throws IOException {		
		return NetworkRobotsClient.connect("127.0.0.1");
	}

	@Test (timeout = 10000) 
	public void testClientDisconnect() throws Exception {
		final long gameId = startGame("TestLevel", "FacingTest", null);
		final SimpleAIFactory aiFactory = new SimpleAIFactory((c, g, e) -> new NoActionAI(c, g, e));
		final NoActionAI ai = (NoActionAI) robotsClient.spawnAI(gameId, "lazyAi", null, aiFactory);
		Assert.assertNotNull(ai);
		
		while (ai.tickCounter < 1) 
			Thread.sleep(10);
		
		robotsClient.close();
		Thread.sleep(1000);
		Assert.assertEquals(1, ai.tickCounter);
		
		final Game game = robotsServer.findGame(gameId);
		Assert.assertNotNull(game);
		Assert.assertEquals(0, game.getTickables().size());
	}
}
