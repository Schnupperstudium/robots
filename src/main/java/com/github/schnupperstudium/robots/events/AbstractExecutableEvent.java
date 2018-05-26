package com.github.schnupperstudium.robots.events;

import com.github.thedwoon.event.Event;

public abstract class AbstractExecutableEvent<T> implements Event {
	private volatile boolean canceled = false;
	private volatile boolean done = false;
	private volatile boolean successful = false;
	
	public AbstractExecutableEvent() {
		
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
	
	public synchronized void executeEvent(T target) {
		if (done)
			return;
		else
			done = true;
		
		// mark it as done before we cancel. May be important for some.
		if (canceled)
			return;
		
		successful = apply(target);
	}
	
	protected abstract boolean apply(T target);
}
