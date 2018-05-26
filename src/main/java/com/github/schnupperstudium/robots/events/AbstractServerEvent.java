package com.github.schnupperstudium.robots.events;

import com.github.schnupperstudium.robots.server.RobotsServer;

public abstract class AbstractServerEvent extends AbstractExecutableEvent<RobotsServer> {
	public AbstractServerEvent() {

	}
	
	public AbstractServerEvent(boolean canCancel) {
		super(canCancel);
	}
}
