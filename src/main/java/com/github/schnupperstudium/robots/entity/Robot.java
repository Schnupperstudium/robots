package com.github.schnupperstudium.robots.entity;

public class Robot extends LivingEntity implements InventoryHolder {
	private static final int ROBOT_HEALTH = 100;
	private static final int INVENTORY_SIZE = 8;
	
	private final Inventory inventory;
	
	public Robot(String name) {
		super(name, ROBOT_HEALTH);
		
		this.inventory = new Inventory(INVENTORY_SIZE);
	}

	public Robot(long uuid, String name) {
		super(uuid, name, ROBOT_HEALTH);
		
		this.inventory = new Inventory(INVENTORY_SIZE);
	}
 
	public Robot(long uuid, String name, Facing facing, int x, int y, int currentHealth, int maxHealth, Inventory inventory) {
		super(uuid, name, facing, x, y, currentHealth, maxHealth);
		
		this.inventory = inventory;
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Robot clone() throws CloneNotSupportedException {
		return new Robot(getUUID(), getName(), getFacing(), getX(), getY(), getCurrentHealth(), getMaxHealth(), getInventory().clone());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
		return result + super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Robot))
			return false;
		
		Robot other = (Robot) obj;
		if (inventory == null) {
			if (other.inventory != null)
				return false;
		} else if (!inventory.equals(other.inventory))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Robot [inventory=" + inventory + ", facing=" + getFacing() + ", getMaxHealth()=" + getMaxHealth()
				+ ", getCurrentHealth()=" + getCurrentHealth() + ", isAlive()=" + isAlive() + ", getUUID()=" + getUUID()
				+ ", getName()=" + getName() + ", getX()=" + getX() + ", getY()=" + getY() + ", getClass()="
				+ getClass() + "]";
	}
}
