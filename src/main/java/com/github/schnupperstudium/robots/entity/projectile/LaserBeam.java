package com.github.schnupperstudium.robots.entity.projectile;

import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class LaserBeam extends Projectile {
	private static final String NAME = "LaserBeam";
	private static final int HEALTH = 20;
	private static final int DAMAGE = 40;
	private static final int SPEED = 1;
	
	public LaserBeam(Facing facing) {
		super(UUIDGenerator.obtain(), NAME, facing, HEALTH);
	}

	public LaserBeam(long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
		super(uuid, name, inventory, facing, x, y, currentHealth, maxHealth);
	}
	
	@Override
	public void updateProjectile(Game game) {
		for (int i = 1; i <= SPEED && isAlive(); i++) {
			moveForward(game);
		}
	}
	
	private void moveForward(Game game) {
		final World world = game.getWorld();
		final Tile currentTile = getTile(world);
		final Tile nextTile = world.getTile(getX() + getFacing().dx, getY() + getFacing().dy);
		if (nextTile.getX() < 0 || nextTile.getX() >= world.getWidth() 
				|| nextTile.getY() < 0 || nextTile.getY() >= world.getHeight() || !nextTile.getMaterial().isVisitable()) {
			currentTile.clearVisitor(this);
			kill();
			return;
		}
		if (nextTile.hasVisitor() && nextTile.getVisitor() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) nextTile.getVisitor();
			livingEntity.damage(DAMAGE);
			currentTile.clearVisitor(this);
			kill();
			return;
		}
		
		currentTile.clearVisitor(this);
		nextTile.setVisitor(this);
	}
	
	@Override
	public Entity clone() throws CloneNotSupportedException {
		return new LaserBeam(getUUID(), getName(), hasInventory() ? getInventory() : null, getFacing(), getX(), getY(), getCurrentHealth(), getMaxHealth());
	}
}
