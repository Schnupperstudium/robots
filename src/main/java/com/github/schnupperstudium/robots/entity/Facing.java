package com.github.schnupperstudium.robots.entity;

public enum Facing {
	NONE(0, 0),
	NORTH(0, -1),
    WEST(-1, 0),
    SOUTH(0, 1),
    EAST(1, 0);

	static {
		NONE.left = NONE;
		NONE.right = NONE;
		NONE.opposite = NONE;
		
        NORTH.left = WEST;
        NORTH.right = EAST;
        NORTH.opposite = SOUTH;

        WEST.left = SOUTH;
        WEST.right = NORTH;
        WEST.opposite = EAST;

        SOUTH.left = EAST;
        SOUTH.right = WEST;
        SOUTH.opposite = NORTH;

        EAST.left = NORTH;
        EAST.right = SOUTH;
        EAST.opposite = WEST;
	}

	public final int dx;
	public final int dy;

	private Facing left;
	private Facing right;
	private Facing opposite;

	Facing(final int dx, final int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public Facing opposite() {
		return opposite;
	}

	public Facing left() {
		return left;
	}

	public Facing right() {
		return right;
	}

	public static Facing of(final int dx, final int dy) {
		if (dx < 0 && dy == 0)
			return WEST;
		if (dx > 0 && dy == 0)
			return EAST;
		if (dx == 0 && dy < 0)
			return NORTH;
		if (dx == 0 && dy > 0)
			return SOUTH;
		
		return NONE;
	}
	
	public static Facing closestFacing(final int dx, final int dy) {
		int offset;
		if (Math.abs(dx) < Math.abs(dy)) {
			offset = dx;
		} else {
			offset = dy;
		}
		
		return Facing.of(dx - offset, dy - offset);
	}
}
