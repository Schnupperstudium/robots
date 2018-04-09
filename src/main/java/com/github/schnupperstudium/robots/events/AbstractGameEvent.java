package com.github.schnupperstudium.robots.events;

import com.github.schnupperstudium.robots.server.GameManager;
import com.github.thedwoon.event.Event;

public abstract class AbstractGameEvent implements Event {
	private volatile boolean canceled = false;
	private volatile boolean done = false;
	private volatile boolean successful = false;
	
	public AbstractGameEvent() {
		
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void cancel() {		
		canceled = true;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	
	public synchronized void executeEvent(GameManager manager) {
		if (done)
			return;
		else
			done = true;
		
		// mark it as done before we cancel. May be important for some.
		if (canceled)
			return;
		
		successful = apply(manager);
	}
	
	protected abstract boolean apply(GameManager manager);
}
