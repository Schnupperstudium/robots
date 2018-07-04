package com.github.schnupperstudium.robots.entity.scenery;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;

public class SmallBoulder extends LivingEntity {
	private static final String NAME = "SmallBoulder";
	private static final int HEALTH = 30; 
	
	public SmallBoulder() {
		super(NAME, HEALTH);
	}
	
	public SmallBoulder(int x, int y) {
		this();
		
		setPosition(x, y);
	}
	
	public SmallBoulder(long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
		super(uuid, name, null, facing, x, y, currentHealth, maxHealth);
	}

	@Override
	public void damage(int damage) {		
		super.damage(damage);
		
		if (isDead() && getWorld() != null) {
			getTile(getWorld()).clearVisitor(this);
		}
	}
	
	@Override
	public SmallBoulder clone() throws CloneNotSupportedException {
		return new SmallBoulder(getUUID(), getName(), null, getFacing(), getX(), getY(), getCurrentHealth(), getMaxHealth());
	}
}
