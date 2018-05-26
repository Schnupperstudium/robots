package com.github.schnupperstudium.robots.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.world.Material;

import javafx.scene.image.Image;

public class Texture {
	private static Logger LOG = LogManager.getLogger();
	
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
					
					LOG.debug("Loading texture: {} [{} x {}]", name, (int) image.getWidth(), (int) image.getHeight());
					TEXTURES.put(name, image);
				}
			} catch (Exception e) {
				LOG.catching(e);
			}
		}
	}
	
	public static Image getTexture(Entity entity) {
		if (entity == null)
			return ERROR_TEXTURE;
		
		final String name = "entity_" + entity.getClass().getSimpleName().toLowerCase();
		return getTexture(name);
	}
	
	public static Image getTexture(Material material) {
		if (material == null)
			return ERROR_TEXTURE;
		
		final String name = "material_" + material.name().toLowerCase();
		return getTexture(name);
		
	}
	
	public static Image getTexture(String textureName) {
		Image texture = TEXTURES.get(textureName);
		if (texture == null)
			return ERROR_TEXTURE;
		else
			return texture;
	}
}
