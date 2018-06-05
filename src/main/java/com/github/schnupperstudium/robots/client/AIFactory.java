package com.github.schnupperstudium.robots.client;

public interface AIFactory {
	AbstractAI createAI(long gameId, long uuid);
}
