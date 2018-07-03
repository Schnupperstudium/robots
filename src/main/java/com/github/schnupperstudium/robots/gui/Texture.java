package com.github.schnupperstudium.robots.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Texture {
	private static final Logger LOG = LogManager.getLogger();
	private static final String SECRET_NAME = new String(Base64.getDecoder().decode("eFh4X1B1c3N5RGVzdHJveWVyX3hYeA=="));
	private static final String TEXTURE_LOCATION = "/textures/";
	private static final Image ERROR_IMAGE = new Image(Texture.class.getResourceAsStream(TEXTURE_LOCATION + "error.png"));
	private static final Texture ERROR_TEXTURE = new Texture(ERROR_IMAGE);
	private static final Texture SPECIAL_ROBOT_TEXTURE = new Texture(new Image(Texture.class.getResourceAsStream(TEXTURE_LOCATION + "entity_robot_xx.png")));
	private static final Map<String, TextureAtlas> TEXTURES = new HashMap<>();
	private static final TextureSelector<?> SELECT_FIRST = (list, obj) -> list.isEmpty() ? ERROR_TEXTURE : list.get(0);
	private static final TextureSelector<Entity> ENTITY_SELECTOR = (list, entity) -> {
		if (entity == null || list.isEmpty())
			return ERROR_TEXTURE;
		
		if (SECRET_NAME.equalsIgnoreCase(entity.getName())) {
			return SPECIAL_ROBOT_TEXTURE;
		}
		
		return list.get((int) (entity.getUUID() % list.size()));
	};
	private static final TextureSelector<Tile> TILE_SELECTOR = (list, tile) -> {
		if (tile == null || list.isEmpty())
			return ERROR_TEXTURE;
		
		int index = (tile.getX() * 7) ^ (tile.getY() * 11);
		return list.get(index % list.size());
	};
	
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
					final PixelReader imageReader = image.getPixelReader();
					final int size = (int) image.getHeight();
					final int totalWidth = (int) image.getWidth();					
					TextureAtlas atlas = new TextureAtlas(totalWidth / size);
					for (int x = 0; x + size <= totalWidth; x += size) {
						 WritableImage texture = new WritableImage(size, size);
						 PixelWriter writer = texture.getPixelWriter();
						 for (int j = 0; j < size; j++) {
							 for (int k = 0; k < size; k++) {
								 writer.setArgb(j, k, imageReader.getArgb(x + j, k));
							 }
						 }
						 
						 atlas.textures.add(new Texture(texture));
					}					
					
					TEXTURES.put(name, atlas);
					LOG.trace("Loaded {} texutures for {} [{} x {}]", atlas.textures.size(), name, size, size);
				}
			} catch (Exception e) {
				LOG.catching(e);
			}
		}
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
		if (entity == null)
			return ERROR_IMAGE;
		
		return getTexture(entity, entity.getFacing());
	}
	
	public static Image getTexture(Entity entity, Facing facing) {
		if (entity == null)
			return ERROR_IMAGE;
				
		final String name = "entity_" + entity.getClass().getSimpleName().toLowerCase();
		return getTexture(name, facing, ENTITY_SELECTOR, entity);
	}
	
	public static Image getTexture(Material material) {
		return getTexture(material, Facing.NORTH);
	}
	
	public static Image getTexture(Tile tile) {
		if (tile == null)
			return ERROR_IMAGE;
		
		final String name = "material_" + tile.getMaterial().name().toLowerCase();
		return getTexture(name, Facing.NORTH, TILE_SELECTOR, tile);
	}
	
	public static Image getTexture(Material material, Facing facing) {
		if (material == null || facing == null)
			return ERROR_IMAGE;
		
		final String name = "material_" + material.name().toLowerCase();
		return getTexture(name, facing);
	}
	
	public static Image getTexture(String textureName) {
		return getTexture(textureName, Facing.NORTH, SELECT_FIRST, null);
	}
	
	public static Image getTexture(String textureName, Facing facing) {
		return getTexture(textureName, facing, SELECT_FIRST, null);
	}
	
	public static <T> Image getTexture(String textureName, Facing facing, TextureSelector<T> selector, T obj) {
		TextureAtlas atlas = TEXTURES.get(textureName);
		if (atlas == null)
			return ERROR_IMAGE;
		
		Texture texture = atlas.select(selector, obj);
		if (texture == null)
			return ERROR_IMAGE;
		
		return texture.select(facing);
	}

	private final Image north;
	private final Image east;
	private final Image south;
	private final Image west;
	
	private Texture(Image image) {
		north = image;
		west = rotateImageRight(north);
		south = rotateImageRight(west);
		east = rotateImageRight(south);
	}

	private Image select(Facing facing) {
		if (facing == Facing.NORTH)
			return north;
		else if (facing == Facing.EAST)
			return east;
		else if (facing == Facing.SOUTH)
			return south;
		else if (facing == Facing.WEST)
			return west;
		else
			return ERROR_IMAGE;
	}
	
	private static class TextureAtlas {
		private final List<Texture> textures;
		
		private TextureAtlas(int initialSize) {
			textures = new ArrayList<>(initialSize);
		}
		
		private <T> Texture select(TextureSelector<T> selector, T obj) {
			return selector.select(textures, obj);
		}
	}
	
	private static interface TextureSelector<T> {
		Texture select(List<Texture> textures, T obj);
	}	
}
