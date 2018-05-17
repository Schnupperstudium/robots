package com.github.schnupperstudium.robots.world;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.io.LevelParser;
import com.github.schnupperstudium.robots.io.WorldParser;
import com.github.schnupperstudium.robots.server.Level;

import junit.framework.Assert;

public final class ParserTest {
	
	@Test
	public void loadLevel() throws IOException, URISyntaxException {
		URL url = getClass().getResource("/level/simple.level");
		Assert.assertNotNull(url);
		
		Level level = LevelParser.loadLevel(new File(url.toURI()));
		Assert.assertNotNull(level);
		Assert.assertEquals("Simple", level.getName());
		Assert.assertEquals("/map/simple.map", level.getMapLocation());
		Assert.assertEquals("unused", level.getGameClass());
		Assert.assertEquals("Das hier ist eine einfache Beschreibung!", level.getDesc());
	}
	
	@Test
	public void loadWorld() throws IOException, URISyntaxException {
		URL url = getClass().getResource("/map/simple.world");
		Assert.assertNotNull(url);
		
		World world = WorldParser.fromFile(new File(url.toURI()));
		Assert.assertNotNull(world);
		Assert.assertEquals(3, world.getWidth());
		Assert.assertEquals(3, world.getHeight());
		
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				Tile tile = world.getTile(x, y);
				Assert.assertEquals(x, tile.getX());
				Assert.assertEquals(y, tile.getY());
				if (x != 1 || y != 1) {
					Assert.assertEquals(Material.VOID, tile.getMaterial());
					Assert.assertNull(tile.getVisitor());
					Assert.assertNull(tile.getItem());
				} else {
					Assert.assertEquals(Material.GRASS, tile.getMaterial());
					Assert.assertNotNull(tile.getVisitor());
					Robot referenceBot = new Robot("TestRobot");
					referenceBot.setPosition(1, 1);
					Assert.assertEquals(referenceBot, tile.getVisitor());
				}
				
			}
		}
	}
}
