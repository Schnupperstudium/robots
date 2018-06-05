package com.github.schnupperstudium.robots.client;

public interface AIFactory {
	AbstractAI createAI(RobotsClient client, long gameId, long uuid);
}
