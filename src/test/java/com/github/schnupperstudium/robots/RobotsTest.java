package com.github.schnupperstudium.robots;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.schnupperstudium.robots.ai.action.EntityAction;
import com.github.schnupperstudium.robots.ai.action.MoveForwardAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.ai.action.TurnRightAction;
import com.github.schnupperstudium.robots.client.AIFactory;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.IWorldObserver;
import com.github.schnupperstudium.robots.client.RobotAI;
import com.github.schnupperstudium.robots.client.RobotsClient;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.World;

import junit.framework.Assert;

public abstract class RobotsTest {
	protected static final EntityAction[] DRIVE_CIRCLE_ACTIONS = new EntityAction[] {
			NoAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			
			TurnRightAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			
			TurnRightAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			
			TurnRightAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			MoveForwardAction.INSTANCE,
			
			TurnRightAction.INSTANCE,
			MoveForwardAction.INSTANCE
	};
	protected static final int[] DRIVE_CIRCLE_DELTA_X = new int[] {
		0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0
	};
	protected static final int[] DRIVE_CIRCLE_DELTA_Y = new int[] {
		0, -1, -1, -1, -1, -1, 0, 1, 1, 1, 1, 1, 0
	};
	protected static final EntityAction[] SPIN_ACTIONS = new EntityAction[] {
			NoAction.INSTANCE,
			TurnRightAction.INSTANCE,
			TurnRightAction.INSTANCE,
			TurnRightAction.INSTANCE,
	};
	protected static final Material[] SPIN_MATERIALS = new Material[] {
			Material.ROCK,
			Material.WATER,
			Material.VOID,
			Material.TREE
	};
	
	protected RobotsServer robotsServer;
	protected RobotsClient robotsClient;
	
	/** setup a bound server. */
	protected abstract RobotsServer setupServer() throws IOException;
	/** setup a client connect to the server. */
	protected abstract RobotsClient setupClient() throws IOException;
	
	@Before
	public void setup() throws IOException {
		robotsServer = setupServer();
		robotsClient = setupClient();
	}
	
	@Test
	public void listLevels() {
		List<Level> levels = robotsClient.listLevels();
		Assert.assertNotNull(levels);
		Assert.assertTrue(levels.size() > 0);
	}
	
	@Test
	public void listGames() {
		List<GameInfo> games = robotsClient.listGames();
		Assert.assertNotNull(games);
	}
	
	@Test
	public void startGame() {
		long gameId = 0;
		assertNoRunningGames();
		gameId = startGame("TestLevel01", "LoadWorldTest", null);
		Assert.assertTrue(UUIDGenerator.isValid(gameId));
		assertGamesRunning(1);
		
		gameId = startGame("TestLevel2", "LoadWorldTest", "myAuth");
		Assert.assertTrue(UUIDGenerator.isValid(gameId));
		List<GameInfo> games = assertGamesRunning(2);
		long securedGames = games.stream().filter(g -> g.hasPassword()).count();
		Assert.assertEquals(1, securedGames);
		
		Game game = robotsServer.findGame(gameId);
		Assert.assertNotNull(game);
		World world = game.getWorld();
		Assert.assertNotNull(world);
		for (int y = 0; y < world.getHeight(); y++) {
			for (int x = 0; x < world.getWidth(); x++) {
				if ((x + y) % 2 == 0) {
					Assert.assertEquals(Material.ROCK, world.getTile(x, y).getMaterial());
				} else {
					Assert.assertEquals(Material.GRASS, world.getTile(x, y).getMaterial());
				}
			}
		}
	}

	@Test (timeout = 1000)
	public void observeWorld() throws Exception {
		final long gameId = startWaterPondLevel("TestLevel", null);
		SimpleObserver observer = new SimpleObserver();
		boolean observable = robotsClient.spawnObserver(gameId, null, observer);
		Assert.assertTrue(observable);
		
		// wait for the server to handle our request and update the observer
		while (observer.callCounter.get() < 1) {
			Thread.sleep(10);
		}
		
		final World world = observer.world;
		Assert.assertNotNull(world);
		Assert.assertEquals(15, world.getWidth());
		Assert.assertEquals(10, world.getHeight());
	}
	
	@Test (timeout = 10000) 
	public void spawnAI() throws Exception {
		long gameId = startWaterPondLevel("TestLevel", null);
		final SimpleAIFactory aiFactory = new SimpleAIFactory((entityUUID) -> new SimpleAI(entityUUID, DRIVE_CIRCLE_ACTIONS));
		final boolean spawned = robotsClient.spawnAI(gameId, "testAI", null, aiFactory);
		Assert.assertTrue(spawned);
		SimpleAI ai = (SimpleAI) aiFactory.lastCreatedAI;
		Assert.assertNotNull(ai);
		
		// tick zero with NO_ACTION is to initialize everything without having to write fancy code
		int tick = 0;
		while (tick <= DRIVE_CIRCLE_ACTIONS.length) {
			while (tick != ai.tickCounter)
				Thread.sleep(10);
			
			if (tick != 0) {				
				Assert.assertEquals(DRIVE_CIRCLE_DELTA_X[tick - 1], ai.getEntity().getX() - ai.spawnX);
				Assert.assertEquals(DRIVE_CIRCLE_DELTA_Y[tick - 1], ai.getEntity().getY() - ai.spawnY);
			}
			tick++;
		}
	}
	
	@Test (timeout = 5000)
	public void testFacing() throws Exception {
		final long gameId = startGame("TestLevel", "FacingTest", null);
		final SimpleAIFactory aiFactory = new SimpleAIFactory((entityUUID) -> new SimpleAI(entityUUID, SPIN_ACTIONS));
		final boolean spawned = robotsClient.spawnAI(gameId, "testAI", null, aiFactory);
		Assert.assertTrue(spawned);
		SimpleAI ai = (SimpleAI) aiFactory.lastCreatedAI;
		Assert.assertNotNull(ai);
		
		// tick zero with NO_ACTION is to initialize everything without having to write fancy code
		int tick = 0;
		while (tick <= SPIN_ACTIONS.length) {
			while (tick != ai.tickCounter)
				Thread.sleep(10);
			
			if (tick != 0) {
				Assert.assertEquals(SPIN_MATERIALS[(0 + tick - 1) % SPIN_MATERIALS.length], ai.getFrontTile().getMaterial());
				Assert.assertEquals(SPIN_MATERIALS[(1 + tick - 1) % SPIN_MATERIALS.length], ai.getRightTile().getMaterial());
				Assert.assertEquals(SPIN_MATERIALS[(2 + tick - 1) % SPIN_MATERIALS.length], ai.getBackTile().getMaterial());
				Assert.assertEquals(SPIN_MATERIALS[(3 + tick - 1) % SPIN_MATERIALS.length], ai.getLeftTile().getMaterial());
			}
			tick++;
		}
	}
	
	protected long startWaterPondLevel(String name, String auth) {
		return startGame(name, "WaterPond", auth);
	}

	protected long startGame(String name, String levelName, String auth) {
		final long gameId = robotsClient.getServerInterface().startGame(name, levelName, auth);
		Assert.assertTrue(UUIDGenerator.isValid(gameId));
		Assert.assertNotNull(robotsServer.findGame(gameId));
		return gameId;
	}
	
	protected List<GameInfo> assertNoRunningGames() {
		return assertGamesRunning(0);
	}
	
	protected List<GameInfo> assertGamesRunning(int amount) {
		List<GameInfo> games = robotsClient.listGames();
		Assert.assertNotNull(games);
		Assert.assertEquals(amount, games.size());
		return games;
	}
	
	@After
	public void shutdown() throws IOException {
		robotsClient.close();
		robotsServer.close();
	}
	
	private final class SimpleObserver implements IWorldObserver {
		private final AtomicInteger callCounter = new AtomicInteger(0);
		private World world = null;
		
		private SimpleObserver() {
			
		}
		
		@Override
		public void updateWorld(long gameId, World world) {
			this.world = world;
			callCounter.incrementAndGet();
		}
	}
	
	private final class SimpleAIFactory implements AIFactory {
		private final AIFactory factory;
		private AbstractAI lastCreatedAI;
		
		private SimpleAIFactory(AIFactory factory) {
			this.factory = factory;
		}
		
		@Override
		public AbstractAI createAI(long uuid) {
			lastCreatedAI = factory.createAI(uuid);
			return lastCreatedAI;
		}
	}
	
	private final class SimpleAI extends RobotAI {
		private final EntityAction[] actions;
		private volatile int tickCounter = 0;
		private int spawnX = 0;
		private int spawnY = 0;
		
		public SimpleAI(long entityUUID, EntityAction[] actions) {
			super(entityUUID);
			
			this.actions = actions;
		}

		@Override
		public EntityAction makeTurn() {		
			return actions[tickCounter++ % actions.length];
		}

		@Override
		public void updateEntity(Entity entity) {
			if (getEntity() == null && entity != null) {
				spawnX = entity.getX();
				spawnY = entity.getY();
			}
			
			super.updateEntity(entity);
		}
	}
}
