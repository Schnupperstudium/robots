package com.github.schnupperstudium.robots.entity.projectile;

import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.server.Game;

public abstract class Projectile extends LivingEntity {

	public Projectile(long uuid, String name, Facing facing, int health) {
		super(uuid, name, health);
		
		setFacing(facing);
	}

	public Projectile(long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
		super(uuid, name, inventory, facing, x, y, currentHealth, maxHealth);
	}
	
	public abstract void updateProjectile(Game game);
}
