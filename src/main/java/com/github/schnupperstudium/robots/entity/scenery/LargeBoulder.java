package com.github.schnupperstudium.robots.entity.scenery;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;

public class LargeBoulder extends LivingEntity {
	private static final String NAME = "BigBoulder";
	private static final int HEALTH = 110; 
	
	public LargeBoulder() {
		super(NAME, HEALTH);
	}
	
	public LargeBoulder(int x, int y) {
		this();
		
		setPosition(x, y);
	}
	
	public LargeBoulder(long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
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
	public LargeBoulder clone() throws CloneNotSupportedException {
		return new LargeBoulder(getUUID(), getName(), null, getFacing(), getX(), getY(), getCurrentHealth(), getMaxHealth());
	}
}
