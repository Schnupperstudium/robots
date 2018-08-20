package com.github.schnupperstudium.robots.world;

/**
 * Defines methods for a simple map.
 * 
 * @author Daniel Wieland
 *
 */
public interface Map {
	/**
	 * @return width of the map.
	 */
	int getWidth();
	/**
	 * @return height of the map.
	 */
	int getHeight();
	/**
	 * Returns the tile at the given location.
	 * The tile must not be <code>null</code>.
	 * 
	 * @param x x coordinate.
	 * @param y y coordinate.
	 * @return tile at the given location.
	 */
	Tile getTile(int x, int y);
	
	/**
	 * @return lower x coordinate bound (included in map).
	 */
	default int getMinX() {
		return 0;
	}
	
	/**
	 * @return upper x coordinate bound (not contained in map).
	 */
	default int getMaxX() {
		return getWidth();
	}
	
	/**
	 * @return lower y coordinate bound (included in map).
	 */
	default int getMinY() {
		return 0;
	}
	
	/**
	 * @return upper y coordinate bound (not contained in map).
	 */
	default int getMaxY() {
		return getHeight();
	}
}
