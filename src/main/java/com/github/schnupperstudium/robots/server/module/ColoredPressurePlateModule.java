package com.github.schnupperstudium.robots.server.module;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class ColoredPressurePlateModule extends AbstractGameListener implements GameModule {

	public ColoredPressurePlateModule() {

	}
	
	@Override
	public void init(RobotsServer server, Game game) {
		game.getMasterGameListener().registerListener(this);
	}

	@Override
	public void onEntityMove(Game game, Entity entity, int sX, int sY) {
		// walk of pressure plate
		final World world = game.getWorld();
		Tile sourceTile = world.getTile(sX, sY);
		Material material = sourceTile.getMaterial();
		
		Material searchedMaterial = null;
		Material replacementMaterial = null; 
		switch (material) {
		case PRESSURE_PLATE_RED:			
			searchedMaterial = Material.GATE_OPEN_RED;
			replacementMaterial = Material.GATE_CLOSED_RED;
			break;
		case PRESSURE_PLATE_GREEN:
			searchedMaterial = Material.GATE_OPEN_GREEN;
			replacementMaterial = Material.GATE_CLOSED_GREEN;
			break;
		case PRESSURE_PLATE_BLUE:
			searchedMaterial = Material.GATE_OPEN_BLUE;
			replacementMaterial = Material.GATE_CLOSED_BLUE;
			break;
		case PRESSURE_PLATE_YELLOW:
			searchedMaterial = Material.GATE_OPEN_YELLOW;
			replacementMaterial = Material.GATE_CLOSED_YELLOW;
			break;
		default:
			break;
		}
		
		if (sourceTile != null && replacementMaterial != null) {
			replaceMaterials(world, searchedMaterial, replacementMaterial);
		}

		searchedMaterial = null;
		replacementMaterial = null;
		
		// walk on pressure plate
		Tile tile = entity.getTile(game.getWorld());
		material = tile.getMaterial();
		switch (material) {
		case PRESSURE_PLATE_RED:
			searchedMaterial = Material.GATE_CLOSED_RED;
			replacementMaterial = Material.GATE_OPEN_RED;
			break;
		case PRESSURE_PLATE_GREEN:
			searchedMaterial = Material.GATE_CLOSED_GREEN;
			replacementMaterial = Material.GATE_OPEN_GREEN;
			break;
		case PRESSURE_PLATE_BLUE:
			searchedMaterial = Material.GATE_CLOSED_BLUE;
			replacementMaterial = Material.GATE_OPEN_BLUE;
			break;
		case PRESSURE_PLATE_YELLOW:
			searchedMaterial = Material.GATE_CLOSED_YELLOW;
			replacementMaterial = Material.GATE_OPEN_YELLOW;
			break;
		default:
			break;
		}
		
		if (sourceTile != null && replacementMaterial != null) {
			replaceMaterials(world, searchedMaterial, replacementMaterial);
		}
	}
	
	private void replaceMaterials(World world, Material searchedMaterial, Material replacementMaterial) {
		final int width = world.getWidth();
		final int height = world.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile t = world.getTile(x, y);
				if (t.getMaterial() == searchedMaterial) 
					t.setMaterial(replacementMaterial);
			}
		}
	}
	
	@Override
	public void onGameEnd(Game game) {
		game.getMasterGameListener().removeListener(this);
	}
}
