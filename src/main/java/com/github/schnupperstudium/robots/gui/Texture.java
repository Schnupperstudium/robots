package com.github.schnupperstudium.robots.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.world.Material;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Texture {
	private static final Logger LOG = LogManager.getLogger();
	private static final String SECRET_NAME = new String(Base64.getDecoder().decode("eFh4X1B1c3N5RGVzdHJveWVyX3hYeA=="));
	private static final String TEXTURE_LOCATION = "/textures/";
	private static final Image ERROR_TEXTURE = new Image(Texture.class.getResourceAsStream(TEXTURE_LOCATION + "error.png"));
	private static final Map<String, Image> TEXTURES = new HashMap<>();
	
	static {
		// load textures
		URL url = Texture.class.getResource(TEXTURE_LOCATION);
		if (url == null) {
			LOG.error("could not locate textures: "  + TEXTURE_LOCATION);			
		} else {
			try {
				File dir = new File(url.toURI());
				for (File file : dir.listFiles()) {
					if (!file.getName().endsWith(".png"))
						continue;
					
					final String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
					InputStream is = new FileInputStream(file);					
					Image image = new Image(is);
					
					// Apparently I rotate the wrong way or it gets rendered the wrong way
					// either way just store the textures the other way round
					LOG.debug("Loading texture: {} [{} x {}]", name, (int) image.getWidth(), (int) image.getHeight());
					for (int w = 0; w > -360; w -= 90) {
						TEXTURES.put(name + ":" + (360 + w) % 360, image);
						
						image = rotateImageRight(image);
					}
				}
			} catch (Exception e) {
				LOG.catching(e);
			}
		}
	}
	
	private Texture() {
		// private constructor
	}
	
	private static Image rotateImageRight(Image input) {
		final int iWidth = (int) input.getWidth();
		final int iHeight = (int) input.getHeight();
		
		WritableImage output = new WritableImage(iHeight, iWidth);
		
		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();
		
		for (int iy = 0; iy < iHeight; iy++) {
			for (int ix = 0; ix < iWidth; ix++) {
				final int argb = reader.getArgb(ix, iy);
				writer.setArgb(iy, iHeight - ix - 1, argb);
			}
		}
		
		return output;
	}
	
	public static Image getTexture(Entity entity) {
		return getTexture(entity, 0);
	}
	
	public static Image getTexture(Entity entity, int rotation) {
		if (entity == null)
			return ERROR_TEXTURE;
		
		if (entity instanceof Robot) {
			return getRobotTexture((Robot) entity, rotation);
		}
		
		final String name = "entity_" + entity.getClass().getSimpleName().toLowerCase();
		return getTexture(name, rotation);
	}
	
	public static Image getRobotTexture(Robot robot, int rotation) {
		if (robot == null)
			return ERROR_TEXTURE;
		
		if (SECRET_NAME.equalsIgnoreCase(robot.getName())) {
			return getTexture("entity_robot_xx", rotation);
		}
		
		int variation = (int) (robot.getUUID() % 15);
		return getTexture("entity_" + robot.getClass().getSimpleName().toLowerCase(), rotation, variation);
	}
	
	public static Image getTexture(Material material) {
		return getTexture(material, 0);
	}
	
	public static Image getTexture(Material material, int rotation) {
		if (material == null)
			return ERROR_TEXTURE;
		
		final String name = "material_" + material.name().toLowerCase();
		return getTexture(name, rotation);
	}
	
	public static Image getTexture(String textureName) {
		return getTexture(textureName, 0);
	}
	
	public static Image getTexture(String textureName, int rotation) {
		Image texture = TEXTURES.get(textureName + ":" + rotation);
		if (texture == null)
			return ERROR_TEXTURE;
		else
			return texture;
	}
	
	public static Image getTexture(String textureName, int rotation, int variation) {
		Image texture = TEXTURES.get(textureName + "_" + variation + ":" + rotation);
		if (texture == null)
			return ERROR_TEXTURE;
		else
			return texture;
	}
}
