package com.github.schnupperstudium.robots.server.tickable;

/**
 * A tick is split into different phases. During each phase different steps
 * are performed. 
 * 
 * @author Daniel Wieland
 *
 */
public enum TickableType {
	/** Pre-tick phase. Modifications can be made before most updates happen. */
	PRE_TICK,
	/** This is where the world gets updated. */
	WORLD_TICK,
	/** This is where AIs will update. */
	ENTITY_TICK,
	/** 
	 * Post-tick phase. Most modifications are made. Final adjustments can be 
	 * performed in this phase.
	 */
	POST_TICK,
	/** Monitor phase. No changes should occur at this point. */
	MONITOR;
}
