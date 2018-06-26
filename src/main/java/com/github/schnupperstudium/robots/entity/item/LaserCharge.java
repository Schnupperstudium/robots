package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.entity.projectile.LaserBeam;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.module.ProjectileModule;

public class LaserCharge extends Item {
	public static final String ITEM_NAME = "LaserCharge";
	
	public LaserCharge() {
		super(ITEM_NAME);
	}
	
	public LaserCharge(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	@Override
	public void use(Game manager, Entity user) {
		if (!user.hasInventory())
			return;
		
		user.getInventory().removeItem(this);
		ProjectileModule module = manager.getModule(ProjectileModule.class);
		if (module == null)
			return;
		
		LaserBeam beam = new LaserBeam(user.getFacing());
		beam.setPosition(user.getX(), user.getY());
		module.addProjectile(beam);
	}

	@Override
	public LaserCharge clone() throws CloneNotSupportedException {
		return new LaserCharge(getUUID(), getFacing(), getX(), getY());
	}
}
