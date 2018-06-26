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
	TELEPORTER_RED(true),
	TELEPORTER_BLUE(true),
	TELEPORTER_GREEN(true),
	TELEPORTER_YELLOW(true);

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
