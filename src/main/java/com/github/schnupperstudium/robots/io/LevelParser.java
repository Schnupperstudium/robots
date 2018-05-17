package com.github.schnupperstudium.robots.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;

import com.github.schnupperstudium.robots.server.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;

public final class LevelParser {
	private static Gson gson;
	
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Level.class, new LevelAdapter());
		gson = builder.create();
	}
	
	private LevelParser() {
		
	}
	
	public static Level loadLevel(String path) throws IOException {
		return loadLevel(new File(path));
	}
	
	public static Level loadLevel(File file) throws IOException {
		JsonReader reader = gson.newJsonReader(new FileReader(file));
		return gson.fromJson(reader, Level.class); 
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		System.out.println(gson.toJson(new Level("default", "map", "desc")));
		
		URL url = LevelParser.class.getResource("/level/default.level");		
		System.out.println(loadLevel(new File(url.toURI())));
	}
	
	private static class LevelAdapter implements JsonDeserializer<Level>, JsonSerializer<Level> {
		private static final String NAME = "name";
		private static final String GAME_LOADER = "gameLoader";
		private static final String MAP_LOADER = "mapLoader";
		private static final String MAP_LOCATION = "mapLocation";
		private static final String DESC = "desc";
		
		@Override
		public JsonElement serialize(Level src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty(NAME, src.getName());
			obj.addProperty(GAME_LOADER, src.getGameLoader());
			obj.addProperty(MAP_LOADER, src.getMapLoader());
			obj.addProperty(MAP_LOCATION, src.getMapLocation());
			obj.addProperty(DESC, src.getDesc());
			
			return obj;
		}

		@Override
		public Level deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if (!json.isJsonObject())
				throw new JsonParseException("expected JsonObject");
			
			JsonObject obj = json.getAsJsonObject();
			
			final String name = getStringOrNull(obj, NAME);
			final String mapLoader = getStringOrNull(obj, MAP_LOADER);
			final String mapLocation = getStringOrNull(obj, MAP_LOCATION);
			final String gameLoader = getStringOrNull(obj, GAME_LOADER);
			final String desc = getStringOrNull(obj, DESC);
			
			return new Level(name, gameLoader, mapLoader, mapLocation, desc);
		}
		
		private String getStringOrNull(JsonObject obj, String name) {
			JsonElement element = obj.get(name);
			if (element == null)
				throw new JsonParseException("expected an element with name: " + name);
			
			if (element.isJsonNull())
				return null;
			else
				return element.getAsString();
		}
	}
}
