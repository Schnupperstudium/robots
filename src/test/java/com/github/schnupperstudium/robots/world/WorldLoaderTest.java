package com.github.schnupperstudium.robots.world;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.schnupperstudium.robots.io.LevelParser;
import com.github.schnupperstudium.robots.server.Level;

public class WorldLoaderTest {
	@Test
	public void loadWorldFromLevelTest() throws Exception {
		InputStream is = getClass().getResourceAsStream("/level/WaterPond.level");
		Assert.assertNotNull(is);
		
		Level level = LevelParser.loadLevel(is);
		Assert.assertNotNull(level);
		
		World world = level.loadWorld();
		Assert.assertNotNull(world);
//		System.out.println(world.toPrettyMapString());
	}
}
