package com.github.schnupperstudium.robots.server.module;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.projectile.Projectile;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;

public class ProjectileModule extends AbstractGameListener implements GameModule {
	private final List<Projectile> projectiles = new ArrayList<>();
	
	public ProjectileModule() {

	}
	
	@Override
	public void init(RobotsServer server, Game game) {
		game.getMasterGameListener().registerListener(this);
	}

	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}
	
	public void removeProjectile(Projectile projectile) {
		projectiles.remove(projectile);
	}
	
	@Override
	public void onRoundComplete(Game game) {
		for (Projectile projectile : new ArrayList<>(projectiles)) {
			projectile.updateProjectile(game);
			if (projectile.isDead())
				removeProjectile(projectile);
				projectile.getTile(game.getWorld()).clearVisitor(projectile);
			}
		}
	}
}
