package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.world.Material;

public class GreenKey extends Key {
	public static final String ITEM_NAME = "GreenKey";
	
	public GreenKey() {
		super(ITEM_NAME);
	}

	public GreenKey(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	@Override
	protected boolean canOpen(Material material) {
		return material == Material.GATE_CLOSED_GREEN;
	}
	
	@Override
	public Key clone() throws CloneNotSupportedException {
		return new GreenKey(getUUID(), getFacing(), getX(), getY());
	}
}
