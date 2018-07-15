package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.world.Material;

public class BlueKey extends Key {
	public static final String ITEM_NAME = "BlueKey";
	
	public BlueKey() {
		super(ITEM_NAME);
	}

	public BlueKey(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	@Override
	protected boolean canOpen(Material material) {
		return material == Material.GATE_CLOSED_BLUE;
	}
	
	@Override
	public Key clone() throws CloneNotSupportedException {
		return new BlueKey(getUUID(), getFacing(), getX(), getY());
	}
}
