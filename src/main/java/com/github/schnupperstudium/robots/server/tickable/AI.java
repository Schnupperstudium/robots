package com.github.schnupperstudium.robots.server.tickable;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class AI implements Tickable {
	private static final Logger LOG = LogManager.getLogger();
	private static final int ROBOT_VISION = 3;
	
	private final Game game;
	private final RobotsClientInterface client;		
	private final LivingEntity entity;
	
	private boolean kicked = false;
	
	public AI(Game game, RobotsClientInterface client, LivingEntity entity) {
		this.game = game;
		this.client = client;
		this.entity = entity;
	}
	
	@Override
	public void update(Game game) {
		updateEntity();
		updateVision(game);
		EntityAction action = makeTurn();
		boolean success = action.apply(game, entity);
		if (success)
			LOG.trace("AI {}:{} made action {}", entity.getName(), entity.getUUID(), action);
		else
			LOG.warn("AI {}:{} failed action {}", entity.getName(), entity.getUUID(), action);
		LOG.trace("ai location ({}, {})", entity.getX(), entity.getY());
	}

	public EntityAction makeTurn() {		
		if (entity.isAlive() && !kicked) {
			EntityAction action = null;
			try {
				action = client.makeTurn(entity.getUUID());
			} catch (Exception e) {
				// catch any exception a client may cause
				kickAI(e.getMessage());
			}
			
			if (action == null) {
				LOG.warn("AI {}:{} failed to return an action for this turn", entity.getName(), entity.getUUID());
				action = NoAction.INSTANCE;
			}
			
			return action;
		} else {
			return NoAction.INSTANCE;
		}
	}
	
	public void updateEntity() {
		if (kicked)
			return;
		
		try {
			client.updateEntity(entity.getUUID(), entity);
		} catch (Exception e) {
			// catch any exception a client may cause
			kickAI(e.getMessage());
		}
	}
	
	public void updateVision(Game game) {
		if (entity.isDead() || kicked)
			return;
		
		final World world = game.getWorld();
		final List<Tile> visibleTiles = new ArrayList<>((ROBOT_VISION + 1) * (ROBOT_VISION + 1));
		final int robotX = entity.getX();
		final int robotY = entity.getY();
		for (int x = robotX - ROBOT_VISION; x <= robotX + ROBOT_VISION; x++) {
//			if (x < 0 || x >= world.getWidth())
//				continue;
			
			for (int y = robotY - ROBOT_VISION; y <= robotY + ROBOT_VISION; y++) {
//				if (y < 0 || y >= world.getHeight())
//					continue;
				
				visibleTiles.add(world.getTile(x, y));
			}
		}
		
		try {
			client.updateVisableTiles(entity.getUUID(), visibleTiles);
		} catch (Exception e) {
			// catch any exception a client may cause
			kickAI(e.getMessage());
		}
	}
	
	private void kickAI(String reason) {
		kicked = true;
		String name = entity != null ? entity.getName() : "<none>";
		long uuid = entity != null ? entity.getUUID() : -1;
		LOG.warn("{}:{} got kicked from {}:{} (Reason: {})", name, uuid, game.getName(), game.getUUID(), reason);
		game.removeTickable(this);
		game.despawnEntity(entity);
	}
	
	@Override
	public TickableType getTickableType() {
		return TickableType.ENTITY_TICK;
	}
	
	public RobotsClientInterface getClient() {
		return client;
	}
	
	public LivingEntity getEntity() {
		return entity;
	}
}
