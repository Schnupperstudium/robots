package com.github.schnupperstudium.robots.client;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

/**
 * An AI controlling a robot.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class RobotAI extends AbstractAI {

	public RobotAI(RobotsClient client, long gameId, long entityUUID) {
		super(client, gameId, entityUUID);
	}

	/**
	 * Searches for the neighboring tile in the given direction.
	 * If there is no tile found it will create a temporary tile with the needed coordinates 
	 * and <code>Material.VOID</code> as material.
	 * 
	 * @param facing direction to search in.
	 * @return tile in the given direction.
	 */
	public Tile getTileByFacing(Facing facing) {
		final int x = getEntity().getX() + facing.dx;
		final int y = getEntity().getY() + facing.dy;
		
		for (Tile tile : getVision()) {
			if (tile.getX() == x && tile.getY() == y)
				return tile;
		}
		
		return new Tile(null, x, y, Material.VOID);
	}
	
	/**
	 * Tile to the left of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getLeftTile() {
		return getTileByFacing(getEntity().getFacing().left());
	}
	
	/**
	 * Tile in front of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getFrontTile() {
		return getTileByFacing(getEntity().getFacing());
	}
	
	/**
	 * Tile to the right of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getRightTile() {
		return getTileByFacing(getEntity().getFacing().right());
	}
	
	/**
	 * Tile behind of the robot. If there is none it will 
	 * return a temporary tile with <code>Material.VOID</code> as material.
	 * 
	 * @return tile to the left of the robot.
	 */
	public Tile getBackTile() {
		return getTileByFacing(getEntity().getFacing().opposite());
	}
	
	public Facing getFacing() {
		return getEntity().getFacing();
	}
	
	@Override
	public Robot getEntity() {		
		return (Robot) super.getEntity();
	}
}
