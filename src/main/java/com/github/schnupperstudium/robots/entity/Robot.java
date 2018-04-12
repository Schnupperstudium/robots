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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facing == null) ? 0 : facing.hashCode());
		result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
		return result + super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Robot))
			return false;
		
		Robot other = (Robot) obj;
		if (facing != other.facing)
			return false;
		if (inventory == null) {
			if (other.inventory != null)
				return false;
		} else if (!inventory.equals(other.inventory))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Robot [inventory=" + inventory + ", facing=" + facing + ", getMaxHealth()=" + getMaxHealth()
				+ ", getCurrentHealth()=" + getCurrentHealth() + ", isAlive()=" + isAlive() + ", getUUID()=" + getUUID()
				+ ", getName()=" + getName() + ", getX()=" + getX() + ", getY()=" + getY() + ", getClass()="
				+ getClass() + "]";
	}
}
