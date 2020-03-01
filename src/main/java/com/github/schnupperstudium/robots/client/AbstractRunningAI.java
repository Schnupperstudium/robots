package com.github.schnupperstudium.robots.client;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.world.Tile;

public abstract class AbstractRunningAI extends AbstractAI {
	private static final Logger LOG = LogManager.getLogger();
	private static final int TURN_BLOCKING_DELAY = 1500;
	private final Thread thread; 

	private volatile EntityAction action;
	private volatile boolean entityUpdated = false;
	private volatile boolean visionUpdated = false;
	
	public AbstractRunningAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
		
		this.thread = new Thread(() -> {
			try {
				synchronized (AbstractRunningAI.this) {
					// wait for initial updates
					while (!entityUpdated || !visionUpdated)
						wait();
				}
				
				run();
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				LOG.catching(e);
			}
			
			despawn();
			LOG.info("{} terminated with {} in game {}", getClass().getSimpleName(), getEntityUUID(), getGameId());
		}, "RunningAI (" + gameId + ", " + entityUUID + ")");
		this.thread.start();
	}

	@Override
	public final EntityAction makeTurn() {
		synchronized (this) {
			long remainingWaitTime = TURN_BLOCKING_DELAY;
			while (action == null && remainingWaitTime > 0) {
				try {
					long waitStart = System.currentTimeMillis();
					wait(remainingWaitTime);
					long waitEnd = System.currentTimeMillis();
					remainingWaitTime = remainingWaitTime - (waitEnd - waitStart);
				} catch (InterruptedException e) {
					return EntityAction.noAction();
				}
			}

			if (action == null) {
				LOG.warn("{} missed action for {} in game {}", getClass().getSimpleName(), getEntity().getName(), getGameId());
				return EntityAction.noAction();
			}
		
			EntityAction selectedAction = action;

			// clear flags for this turn
			entityUpdated = false;
			visionUpdated = false;
			action = null;
			
			// notify waiting threads
			this.notifyAll();

			return selectedAction;
		}
	}

	@Override
	public void updateEntity(Entity entity) {
		super.updateEntity(entity);
		
		synchronized (this) {
			entityUpdated = true;
			this.notifyAll();
		}
	}
	
	@Override
	public void updateVision(List<Tile> tiles) {		
		super.updateVision(tiles);
		
		synchronized (this) {
			visionUpdated = true;
			this.notifyAll();
		}
	}
	
	protected abstract void run() throws InterruptedException;
	
	public void turnLeft() throws InterruptedException {
		waitForAction(EntityAction.turnLeft());
	}
	
	public void turnRight() throws InterruptedException {
		waitForAction(EntityAction.turnRight());
	}
	
	public void driveForward() throws InterruptedException {
		waitForAction(EntityAction.moveForward());		
	}
	
	public void driveBackwards() throws InterruptedException {
		waitForAction(EntityAction.moveBackward());
	}
	
	public void dropItem(Item item) throws InterruptedException {
		waitForAction(EntityAction.dropItem(item));
	}

	public void pickUpItem() throws InterruptedException {
		waitForAction(EntityAction.pickUpItem());
	}
	
	public void useItem(Item item) throws InterruptedException {
		waitForAction(EntityAction.useItem(item));
	}
	
	protected final void waitForAction(EntityAction action) throws InterruptedException {
		synchronized (this) {
			checkForInterrupt();
			this.action = action;
			this.notifyAll();
			
			waitForActionCompletion();
		}
	}
	
	protected final void waitForActionCompletion() throws InterruptedException {
		synchronized (this) {
			// if an action is set wait until it is cleared by makeTurn
			while (action != null) {
				checkForInterrupt();
				wait();
			}

			// wait for updates in case we get a race
			while (!entityUpdated || !visionUpdated) {
				checkForInterrupt();
				wait();
			}
		}
	}
	
	private void checkForInterrupt() throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException("thread was interrupted");
	}
	
	@Override
	public boolean despawn() {
		thread.interrupt();
		return super.despawn();
	}
}
