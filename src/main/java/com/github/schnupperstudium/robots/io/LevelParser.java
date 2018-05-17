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
		System.out.println(gson.toJson(new Level("default", "game", "map", "desc")));
		
		URL url = LevelParser.class.getResource("/level/default.level");		
		System.out.println(loadLevel(new File(url.toURI())));
	}
	
	private static class LevelAdapter implements JsonDeserializer<Level>, JsonSerializer<Level> {
		private static final String NAME = "name";
		private static final String GAME_CLASS = "gameClass";
		private static final String MAP_LOCATION = "mapLocation";
		private static final String DESC = "desc";
		
		@Override
		public JsonElement serialize(Level src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty(NAME, src.getName());
			obj.addProperty(GAME_CLASS, src.getGameClass());
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
			
			final String name = getElement(obj, NAME).getAsString();
			final String mapLocation = getElement(obj, MAP_LOCATION).getAsString();
			final String gameClass = getElement(obj, GAME_CLASS).getAsString();
			final String desc = getElement(obj, DESC).getAsString();
			
			return new Level(name, gameClass, mapLocation, desc);
		}
		
		private JsonElement getElement(JsonObject obj, String name) {
			JsonElement element = obj.get(name);
			if (element == null)
				throw new JsonParseException("expected an element with name: " + name);
			
			return element;
		}
	}
}
