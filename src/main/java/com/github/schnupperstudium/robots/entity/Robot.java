package com.github.schnupperstudium.robots.entity;

public class Robot extends LivingEntity implements InventoryHolder {
	private static final int ROBOT_HEALTH = 100;
	private static final int INVENTORY_SIZE = 8;
	
	private final Inventory inventory = new Inventory(INVENTORY_SIZE);
	
	private Facing facing = Facing.NORTH;	
	
	public Robot(String name) {
		super(name, ROBOT_HEALTH);
	}

	public Robot(long uuid, String name) {
		super(uuid, name, ROBOT_HEALTH);
	}
 
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	public Facing getFacing() {
		return facing;
	}
	
	public void setFacing(Facing facing) {
		this.facing = facing;
	}
}
