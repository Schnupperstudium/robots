package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.world.Material;

public class RedKey extends Key {
	public static final String ITEM_NAME = "RedKey";
	
	public RedKey() {
		super(ITEM_NAME);
	}

	public RedKey(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	@Override
	protected boolean canOpen(Material material) {
		return material == Material.GATE_CLOSED_RED;
	}
	
	@Override
	public Key clone() throws CloneNotSupportedException {
		return new RedKey(getUUID(), getFacing(), getX(), getY());
	}
}
