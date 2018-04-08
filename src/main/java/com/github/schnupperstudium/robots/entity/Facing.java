package com.github.schnupperstudium.robots.entity;

public enum Facing {
	NORTH(0, -1), WEST(-1, 0), SOUTH(0, 1), EAST(1, 0);

	public final int dx;
	public final int dy;

	Facing(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public Facing opposite() {
		switch (this) {
		case NORTH:
			return SOUTH;
		case WEST:
			return EAST;
		case SOUTH:
			return NORTH;
		case EAST:
			return WEST;
		default:
			return null;
		}
	}

	public Facing left() {
		switch (this) {
		case NORTH:
			return WEST;
		case WEST:
			return SOUTH;
		case SOUTH:
			return EAST;
		case EAST:
			return NORTH;
		default:
			return null;
		}
	}

	public Facing right() {
		switch (this) {
		case NORTH:
			return EAST;
		case WEST:
			return NORTH;
		case SOUTH:
			return WEST;
		case EAST:
			return SOUTH;
		default:
			return null;
		}
	}

	public static Facing of(int dx, int dy) {
		if (dx < 0 && dy == 0)
			return WEST;
		else if (dx > 0 && dy == 0)
			return EAST;
		else if (dx == 0 && dy < 0)
			return NORTH;
		else if (dx == 0 && dy > 0)
			return SOUTH;
		else
			throw new IllegalArgumentException("Invalid facing: dx = " + dx + ", dy = " + dy);
	}
}
