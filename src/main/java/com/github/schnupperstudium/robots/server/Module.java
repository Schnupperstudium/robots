package com.github.schnupperstudium.robots.server;

import com.github.thedwoon.event.EventListener;

public interface Module extends EventListener {
	void updateModule();
	void load(GameManager gameManager);
	void unload();
}
