package com.github.schnupperstudium.robots.server.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractObservable<T> {
	private final List<T> toAdd;
	private final List<T> toRemove;
	private final List<T> gameListeners;
	
	public AbstractObservable() {
		this.toAdd = new ArrayList<>();
		this.toRemove = new ArrayList<>();
		this.gameListeners = new ArrayList<>();
	}
	
	public final void registerListener(T listener) {
		if (listener == null)
			return;
		
		synchronized (toAdd) {
			toAdd.add(listener);
		}
	}
	
	public final void removeListener(T listener) {
		if (listener == null)
			return;
		
		synchronized (toRemove) {
			toRemove.add(listener);
		}
	}
	
	protected final void notifyListeners(Consumer<T> consumer) {		
		synchronized (gameListeners) {
			updateList();
			
			gameListeners.forEach(consumer);
		}
	}
	
	protected final boolean consultListeners(Function<T, Boolean> mapper) {
		synchronized (gameListeners) {
			updateList();
			
			for (T listener : gameListeners) {
				Boolean result = mapper.apply(listener);
				if (result == Boolean.FALSE)
					return false;
			}
		}
		
		return true;
	}
	
	private void updateList() {
		synchronized (gameListeners) {
			if (!toRemove.isEmpty()) {
				synchronized (toRemove) {
					toRemove.forEach(gameListeners::remove);
					toRemove.clear();
				}
			}
			
			if (!toAdd.isEmpty()) {
				synchronized (toAdd) {
					toAdd.forEach(gameListeners::add);
					toAdd.clear();
				}
			}
		}
	}
}
