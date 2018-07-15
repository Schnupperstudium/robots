package com.github.schnupperstudium.robots.server.module;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GateModule extends AbstractGameListener implements GameModule {
	private final int sX;
	private final int sY;
	private final int tX;
	private final int tY;
	
	public GateModule(JsonElement element) {
		if (element == null || !element.isJsonObject())
			throw new IllegalArgumentException("missing parameters");
		
		JsonObject obj = element.getAsJsonObject();
		sX = obj.get("sX").getAsInt();
		sY = obj.get("sY").getAsInt();
		tX = obj.get("tX").getAsInt();
		tY = obj.get("tY").getAsInt();
	}
	
	@Override
	public void onEntityMove(Game game, Entity entity, int sX, int sY) {
		// walk off pressure plate || walk on pressure plate
		if ((this.sX == sX && this.sY == sY) || (entity.getX() == this.sX && entity.getY() == this.sY)) {
			final Tile targetTile = game.getWorld().getTile(tX, tY);
			targetTile.setMaterial(getNextGateState(targetTile.getMaterial()));
		}
	}
	
	private Material getNextGateState(Material currentMaterial) {
		switch (currentMaterial) {
		case GATE_OPEN:
			return Material.GATE_CLOSED;
		case GATE_CLOSED:
			return Material.GATE_OPEN;
			
		case GATE_OPEN_RED:
			return Material.GATE_CLOSED_RED;
		case GATE_CLOSED_RED:
			return Material.GATE_OPEN_RED;

		case GATE_OPEN_BLUE:
			return Material.GATE_CLOSED_BLUE;
		case GATE_CLOSED_BLUE:
			return Material.GATE_OPEN_BLUE;

		case GATE_OPEN_GREEN:
			return Material.GATE_CLOSED_GREEN;
		case GATE_CLOSED_GREEN:
			return Material.GATE_OPEN_GREEN;
			
		case GATE_OPEN_YELLOW:
			return Material.GATE_CLOSED_YELLOW;
		case GATE_CLOSED_YELLOW:
			return Material.GATE_OPEN_YELLOW;
		default:
			return Material.VOID;	
		}
	}
	
	@Override
	public void init(RobotsServer server, Game game) {
		game.getMasterGameListener().registerListener(this);
	}

	@Override
	public void onGameEnd(Game game) {
		game.getMasterGameListener().removeListener(this);
	}
}
