package com.github.schnupperstudium.robots;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;
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
		Assert.assertEquals(15, world.getWidth());
		Assert.assertEquals(10, world.getHeight());
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
		final long gameId = startGame("TestLevel2", "LoadWorldTest", null);
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
	
	@Test(timeout = 3000) 
	public void unobserveWorld() throws Exception {
		final long gameId = startGame("TestLevel2", "LoadWorldTest", null);
		SimpleObserver observer = new SimpleObserver();
		boolean observable = robotsClient.spawnObserver(gameId, null, observer);
		Assert.assertTrue(observable);
		
		while (observer.callCounter.get() < 1) {
			Thread.sleep(10);
		}
		
		Assert.assertNotNull(observer.world);
		robotsClient.despawnObserver(gameId, observer);
		observer.world = null;
		int callCount = observer.callCounter.get();
		
		Thread.sleep(1000);
		Assert.assertEquals(callCount, observer.callCounter.get());
		Assert.assertNull(observer.world);
	}
	
	@Test (timeout = 10000) 
	public void spawnAI() throws Exception {
		final List<EntityLocation> locations = new LinkedList<>();
		Consumer<SimpleAI> checks = new Consumer<RobotsTest.SimpleAI>() {
			@Override
			public void accept(SimpleAI ai) {
				locations.add(new EntityLocation(ai.getX(), ai.getY(), ai.getFacing()));
			}
		};
		final long gameId = startWaterPondLevel("TestLevel", null);
		final SimpleAIFactory aiFactory = new SimpleAIFactory((gId, eId) -> new SimpleAI(gId, eId, DRIVE_CIRCLE_ACTIONS, checks));
		final AbstractAI spawned = robotsClient.spawnAI(gameId, "testAI", null, aiFactory);
		Assert.assertNotNull(spawned);
		SimpleAI ai = (SimpleAI) aiFactory.lastCreatedAI;
		Assert.assertNotNull(ai);
		
		while (ai.tickCounter < DRIVE_CIRCLE_ACTIONS.length)
			Thread.sleep(10);
		
		for (int i = 0; i < locations.size(); i++) {
			EntityLocation location = locations.get(i);	
			Assert.assertEquals(DRIVE_CIRCLE_DELTA_X[i], location.x - ai.spawnX);
			Assert.assertEquals(DRIVE_CIRCLE_DELTA_Y[i], location.y - ai.spawnY);
		}
	}
	
	@Test (timeout = 5000)
	public void despawnAI() throws Exception {
		final long gameId = startWaterPondLevel("TestLevel", null);
		final SimpleAI ai = (SimpleAI) robotsClient.spawnAI(gameId, "testAI", null, (gId, eId) -> new SimpleAI(gId, eId, DRIVE_CIRCLE_ACTIONS, e -> {}));
		Assert.assertNotNull(ai);

		while (ai.tickCounter < 1)
			Thread.sleep(10);
		
		Assert.assertTrue(robotsClient.despawnAI(ai));
		int tick = ai.tickCounter;
		
		Thread.sleep(1000);
		Assert.assertEquals(tick, ai.tickCounter);
	}
	
	@Test (timeout = 10000)
	public void testFacing() throws Exception {
		List<EntityVisinity> visinities = new LinkedList<>();
		Consumer<SimpleAI> checks = new Consumer<RobotsTest.SimpleAI>() {
			@Override
			public void accept(SimpleAI ai) {
				visinities.add(new EntityVisinity(ai.getLeftTile(), ai.getRightTile(), ai.getFrontTile(), ai.getBackTile()));
			}
		};
		
		final long gameId = startGame("TestLevel", "FacingTest", null);
		final SimpleAIFactory aiFactory = new SimpleAIFactory((gId, eId) -> new SimpleAI(gId, eId, SPIN_ACTIONS, checks));
		final AbstractAI spawned = robotsClient.spawnAI(gameId, "testAI", null, aiFactory);
		Assert.assertNotNull(spawned);
		SimpleAI ai = (SimpleAI) aiFactory.lastCreatedAI;
		Assert.assertNotNull(ai);
		
		while (visinities.size() < SPIN_ACTIONS.length)
			Thread.sleep(10);		
		
		for (int i = 0; i < visinities.size(); i++) {
			EntityVisinity visinity = visinities.get(i);
			Assert.assertEquals(SPIN_MATERIALS[(0 + i) % SPIN_MATERIALS.length], visinity.front.getMaterial());
			Assert.assertEquals(SPIN_MATERIALS[(1 + i) % SPIN_MATERIALS.length], visinity.right.getMaterial());
			Assert.assertEquals(SPIN_MATERIALS[(2 + i) % SPIN_MATERIALS.length], visinity.back.getMaterial());
			Assert.assertEquals(SPIN_MATERIALS[(3 + i) % SPIN_MATERIALS.length], visinity.left.getMaterial());
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
		public AbstractAI createAI(long gameId, long uuid) {
			lastCreatedAI = factory.createAI(gameId, uuid);
			return lastCreatedAI;
		}
	}
	
	private final class SimpleAI extends RobotAI {
		private final EntityAction[] actions;
		private final Consumer<SimpleAI> update;
		private volatile int tickCounter = -1;
		private int spawnX = 0;
		private int spawnY = 0;
		
		public SimpleAI(long gameId, long entityUUID, EntityAction[] actions, Consumer<SimpleAI> update) {
			super(gameId, entityUUID);
			
			this.actions = actions;
			this.update = update;
		}

		@Override
		public EntityAction makeTurn() {
			EntityAction action = null;
			if (tickCounter == -1) {
				action = NoAction.INSTANCE;
			} else {
				action = actions[tickCounter % actions.length];				
			}
			tickCounter++;
			return action;
		}

		@Override
		public void updateEntity(Entity entity) {
			if (getEntity() == null && entity != null) {
				spawnX = entity.getX();
				spawnY = entity.getY();
			} else if (tickCounter > 1)
				update.accept(this);
			
			super.updateEntity(entity);			
		}
	}
	
	private static final class EntityLocation {
		public final int x;
		public final int y;
		public final Facing facing;
		
		private EntityLocation(int x, int y, Facing facing) {
			this.x = x;
			this.y = y;
			this.facing = facing;
		}

		@Override
		public String toString() {
			return "EntityLocation [x=" + x + ", y=" + y + ", facing=" + facing + "]";
		}
	}
	
	private static final class EntityVisinity {
		public final Tile left;
		public final Tile right;
		public final Tile front;
		public final Tile back;
		
		private EntityVisinity(Tile left, Tile right, Tile front, Tile back) {
			this.left = left;
			this.right = right;
			this.front = front;
			this.back = back;
		}

		@Override
		public String toString() {
			return "EntityVisinity [left=" + left + ", right=" + right + ", front=" + front + ", back=" + back + "]";
		}
	}
}
