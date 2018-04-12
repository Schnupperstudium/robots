package com.github.schnupperstudium.robots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;

public final class WorldParser {
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String TILES = "tiles";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String MATERIAL = "material";
	private static final String VISITOR = "visitor";
	private static final String VISITOR_CLASS = "visitorClass";
	private static final String ITEM = "item";
	private static final String ITEM_CLASS = "itemClass";
	
	private static Gson gson;
	
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(World.class, new WorldAdapter());
		builder.registerTypeAdapter(Tile.class, new TileAdapter());
		gson = builder.create();
	}
	
	public static String toJson(World world) {
		return gson.toJson(world);
	}
	
	public static World fromJson(String json) {
		return gson.fromJson(json, World.class);
	}
	
	public static World fromFile(File file) throws FileNotFoundException {
		JsonReader reader = gson.newJsonReader(new FileReader(file));
		return gson.fromJson(reader, World.class); 
	}
	
	public static void main(String[] args) {
		World world = new World(3, 3);
		world.getTile(1, 1).setMaterial(Material.GRASS);
		world.getTile(1, 1).setVisitor(new Robot("TestRobot"));
		
		String worldJson = gson.toJson(world);
		System.out.println(worldJson);
		World world2 = gson.fromJson(worldJson, World.class);
		System.out.println(world2);
	}
	
	private static final class WorldAdapter implements JsonSerializer<World>, JsonDeserializer<World> {
		
		@Override
		public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			final int width = obj.get(WIDTH).getAsInt();
			final int height = obj.get(HEIGHT).getAsInt();
			
			List<Tile> tiles = new LinkedList<>();
			JsonArray jsonTiles = obj.get(TILES).getAsJsonArray();
			for (JsonElement element : jsonTiles) {				
				tiles.add(context.deserialize(element, Tile.class));				
			}
			
			return new World(width, height, tiles);
		}

		@Override
		public JsonElement serialize(World src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty(WIDTH, src.getWidth());
			obj.addProperty(HEIGHT, src.getHeight());
			
			JsonArray tiles = new JsonArray();
			for (int x = 0; x < src.getWidth(); x++) {
				for (int y = 0; y < src.getHeight(); y++) {
					Tile tile = src.getTile(x, y);
					tiles.add(context.serialize(tile, Tile.class));
				}
			}
			
			obj.add(TILES, tiles);
			
			return obj;
		}
	}
	
	private static final class TileAdapter implements JsonSerializer<Tile>, JsonDeserializer<Tile> {

		@Override
		public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {	
			JsonObject obj = json.getAsJsonObject();
			final int x = obj.get(X).getAsInt();
			final int y = obj.get(Y).getAsInt();
			final Material material = context.deserialize(obj.get(MATERIAL), Material.class);
			Entity visitor = null;
			if (obj.has(VISITOR_CLASS)) {
				final String className = obj.get(VISITOR_CLASS).getAsString();
				try {
					Class<?> clazz = Class.forName(className);
					visitor = context.deserialize(obj.get(VISITOR), clazz);
				} catch (ClassNotFoundException e) {
					throw new JsonParseException("unkown class: " + className, e);
				}
			}
			
			Item item = null;
			if (obj.has(ITEM_CLASS)) {
				final String className = obj.get(ITEM_CLASS).getAsString();
				try {
					Class<?> clazz = Class.forName(className);
					item = context.deserialize(obj.get(ITEM), clazz);
				} catch (ClassNotFoundException e) {
					throw new JsonParseException("unkown class: " + className, e);
				}
			}
			
			Tile tile = new Tile(null, x, y, material);
			tile.setItem(item);
			tile.setVisitor(visitor);
			
			return tile;
		}

		@Override
		public JsonElement serialize(Tile src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty(X, src.getX());
			obj.addProperty(Y, src.getY());
			obj.add(MATERIAL, context.serialize(src.getMaterial(), Material.class));
			final Item item = src.getItem();
			if (item != null) {
				obj.addProperty(ITEM_CLASS, item.getClass().getName());
				obj.add(ITEM, context.serialize(item));
			}
			
			final Entity visitor = src.getVisitor();
			if (visitor != null) {
				obj.addProperty(VISITOR_CLASS, visitor.getClass().getName());
				obj.add(VISITOR, context.serialize(visitor));
			}
			
			return obj;
		}
		
	}
}
