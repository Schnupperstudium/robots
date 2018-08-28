package com.github.schnupperstudium.robots.world;

import com.github.schnupperstudium.robots.entity.Facing;

/**
 * Marks a location on a map.
 * 
 * @author Daniel Wieland
 *
 */
public class Location {
	private final int x;
	private final int y;
	private final Facing facing;
	
	/**
	 * Creates a new location with <code>Facing.NONE</code> as facing. 
	 * 
	 * @param x x coordinate.
	 * @param y y coordinate.
	 */
	public Location(int x, int y) {
		this(x, y, Facing.NONE);
	}
	
	/**
	 * Creates a new location. 
	 * 
	 * @param x x coordinate.
	 * @param y y coordinate.
	 * @param facing facing.
	 */
	public Location(int x, int y, Facing facing) {
		this.x = x;
		this.y = y;
		this.facing = facing;
	}
	
	/**
	 * @return x coordinate.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return facing.
	 */
	public Facing getFacing() {
		return facing;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
