package com.github.schnupperstudium.robots.world;

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
	SCORCHED_EARTH(true);

	private final boolean visitable;

	private Material(boolean visitiable) {
		this.visitable = visitiable;
	}

	public final boolean isVisitable() {
		return visitable;
	}
}
