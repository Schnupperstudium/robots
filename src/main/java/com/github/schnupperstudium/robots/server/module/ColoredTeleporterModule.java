package com.github.schnupperstudium.robots.server.module;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class ColoredTeleporterModule extends AbstractGameListener implements GameModule {
		
	public ColoredTeleporterModule() {

	}
	
	@Override
	public void init(RobotsServer server, Game game) {
		game.getMasterGameListener().registerListener(this);
	}

	@Override
	public void onEntityMove(Game game, Entity entity) {
		Tile tile = entity.getTile(game.getWorld());
		Material material = tile.getMaterial();
		if (material != Material.TELEPORTER_RED && material != Material.TELEPORTER_GREEN 
				&& material != Material.TELEPORTER_BLUE && material != Material.TELEPORTER_YELLOW)
			return;
		
		World world = game.getWorld();
		int width = world.getWidth();
		int height = world.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile t = world.getTile(x, y);
				if (t.getX() == tile.getX() && t.getY() == tile.getY())
					continue;
				
				if (t.getMaterial() == material) {
					// teleport entity
					tile.setVisitor(null);
					t.setVisitor(entity);
					return;
				}
			}
		}
	}
}
