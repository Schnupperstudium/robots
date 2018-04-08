package com.github.schnupperstudium.robots.events;

import com.github.thedwoon.event.Event;

public abstract class AbstractEvent implements Event {
	private volatile boolean canceled = false;
	
	public AbstractEvent() {
		
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void cancel() {
		canceled = true;
	}
}
