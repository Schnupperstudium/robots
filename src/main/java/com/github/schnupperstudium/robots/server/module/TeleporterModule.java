package com.github.schnupperstudium.robots.server.module;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.AbstractGameListener;
import com.github.schnupperstudium.robots.world.Tile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TeleporterModule extends AbstractGameListener implements GameModule {
	private final int sX;
	private final int sY;
	private final int tX;
	private final int tY;
	
	public TeleporterModule(JsonElement element) {
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
		if (entity.getX() != this.sX || entity.getY() != this.sY)
			return;
				
		Tile sourceTile = entity.getTile(game.getWorld());
		Tile targetTile = game.getWorld().getTile(tX, tY);
		sourceTile.clearVisitor(entity);
		targetTile.setVisitor(entity);
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
