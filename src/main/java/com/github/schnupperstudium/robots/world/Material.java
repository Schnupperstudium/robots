package com.github.schnupperstudium.robots.world;

import com.github.schnupperstudium.robots.entity.Entity;

/**
 * This enum represents the materials known to the game.
 *
 * @author TheDwoon
 */
public enum Material {
	MAP_BORDER(false),
	VOID(false),
	GRASS(true),
	TREE(false),
	ROCK(false),
	WATER(false),
	SPAWN(true),
	SCORCHED_EARTH(true),
	
	// teleporter
	TELEPORTER(true),
	TELEPORTER_RED(true),
	TELEPORTER_BLUE(true),
	TELEPORTER_GREEN(true),
	TELEPORTER_YELLOW(true),
	
	// pressure plates
	PRESSURE_PLATE(true),
	PRESSURE_PLATE_RED(true),
	PRESSURE_PLATE_BLUE(true),
	PRESSURE_PLATE_GREEN(true),
	PRESSURE_PLATE_YELLOW(true),
	
	// gates
	GATE_CLOSED(false),
	GATE_OPEN(true),
	GATE_CLOSED_RED(false),
	GATE_OPEN_RED(true),
	GATE_CLOSED_BLUE(false),
	GATE_OPEN_BLUE(true),
	GATE_CLOSED_GREEN(false),
	GATE_OPEN_GREEN(true),
	GATE_CLOSED_YELLOW(false),
	GATE_OPEN_YELLOW(true);

	private final boolean visitable;

	private Material(boolean visitiable) {
		this.visitable = visitiable;
	}

	/**
	 * @return true if an {@link Entity} can drive on these
	 */
	public final boolean isVisitable() {
		return visitable;
	}
}
