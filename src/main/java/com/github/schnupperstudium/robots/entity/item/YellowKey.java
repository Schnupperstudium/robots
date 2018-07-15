package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.world.Material;

public class YellowKey extends Key {
	public static final String ITEM_NAME = "YellowKey";
	
	public YellowKey() {
		super(ITEM_NAME);
	}

	public YellowKey(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	@Override
	protected boolean canOpen(Material material) {
		return material == Material.GATE_CLOSED_YELLOW;
	}
	
	@Override
	public Key clone() throws CloneNotSupportedException {
		return new YellowKey(getUUID(), getFacing(), getX(), getY());
	}
}
