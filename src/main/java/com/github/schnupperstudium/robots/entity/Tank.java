package com.github.schnupperstudium.robots.entity;

public class Tank extends LivingEntity {
	private static final int ROBOT_HEALTH = 200;
	private static final int INVENTORY_SIZE = 8;
	
	public Tank(String name) {
		super(name, ROBOT_HEALTH);
		
		setInventory(new Inventory(INVENTORY_SIZE));
	}

	public Tank(long uuid, String name) {
		super(uuid, name, ROBOT_HEALTH);
		
		setInventory(new Inventory(INVENTORY_SIZE));
	}
 
	public Tank(long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
		super(uuid, name, inventory, facing, x, y, currentHealth, maxHealth);
	}
	
	@Override
	public Tank clone() throws CloneNotSupportedException {
		return new Tank(getUUID(), getName(), getInventory().clone(), getFacing(), getX(), getY(), getCurrentHealth(), getMaxHealth());
	}

	@Override
	public String toString() {
		return "Tank [inventory=" + getInventory() + ", facing=" + getFacing() + ", getMaxHealth()=" + getMaxHealth()
				+ ", getCurrentHealth()=" + getCurrentHealth() + ", isAlive()=" + isAlive() + ", getUUID()=" + getUUID()
				+ ", getName()=" + getName() + ", getX()=" + getX() + ", getY()=" + getY() + ", getClass()="
				+ getClass() + "]";
	}
}
