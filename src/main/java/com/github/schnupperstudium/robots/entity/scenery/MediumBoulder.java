package com.github.schnupperstudium.robots.entity.scenery;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;

public class MediumBoulder extends LivingEntity {
	private static final String NAME = "MediumBoulder";
	private static final int HEALTH = 70; 
	
	public MediumBoulder() {
		super(NAME, HEALTH);
	}
	
	public MediumBoulder(int x, int y) {
		this();
		
		setPosition(x, y);
	}
	
	public MediumBoulder(long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
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
	public MediumBoulder clone() throws CloneNotSupportedException {
		return new MediumBoulder(getUUID(), getName(), null, getFacing(), getX(), getY(), getCurrentHealth(), getMaxHealth());
	}
}
