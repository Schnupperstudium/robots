package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class Key extends Item {	
	public static final String ITEM_NAME = "Key";
		
	public Key() {
		super(ITEM_NAME);
	}

	public Key(String name) {
		super(name);
	}
	
	public Key(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	public Key(long uuid, String name, Facing facing, int x, int y) {
		super(uuid, name, facing, x, y);
	}
	
	@Override
	public void use(Game game, Entity user) {
		World world = game.getWorld();
		Tile tile = world.getTile(user.getX(), user.getY(), user.getFacing());
		if (canOpen(tile.getMaterial())) {
			Material nextMaterial = getNextMaterial(tile.getMaterial());
			if (nextMaterial != null)
				tile.setMaterial(nextMaterial);
		}
	}

	protected Material getNextMaterial(Material material) {
		switch (material) {
		case GATE_CLOSED:
			return Material.GATE_OPEN;
		case GATE_CLOSED_RED:
			return Material.GATE_OPEN_RED;
		case GATE_CLOSED_GREEN:
			return Material.GATE_OPEN_GREEN;
		case GATE_CLOSED_BLUE:
			return Material.GATE_OPEN_BLUE;
		case GATE_CLOSED_YELLOW:
			return Material.GATE_OPEN_YELLOW;
		default:
			return null;
		}
	}
	
	protected boolean canOpen(Material material) {		
		return material == Material.GATE_CLOSED;
	}
	
	@Override
	public Key clone() throws CloneNotSupportedException {
		return new Key(getUUID(), getName(), getFacing(), getX(), getY());
	}
}
