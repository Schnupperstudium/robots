package com.github.schnupperstudium.robots.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.schnupperstudium.robots.entity.Robot;
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
	
	public static Level loadLevel(InputStream is) {
		JsonReader reader = gson.newJsonReader(new InputStreamReader(is));
		return gson.fromJson(reader, Level.class);
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
		private static final String MODULES = "modules";
		private static final String MAP_LOADER = "mapLoader";
		private static final String MAP_LOCATION = "mapLocation";
		private static final String DESC = "desc";
		private static final String ALLOWED_ENTITIES = "allowedEntities";
		
		@Override
		public JsonElement serialize(Level src, Type typeOfSrc, JsonSerializationContext context) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Level deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if (!json.isJsonObject())
				throw new JsonParseException("expected JsonObject");
			
			JsonObject obj = json.getAsJsonObject();
			
			final String name = getStringOrNull(obj, NAME);
			final String mapLoader = getStringOrNull(obj, MAP_LOADER, false);
			final String mapLocation = getStringOrNull(obj, MAP_LOCATION);
			final String desc = getStringOrNull(obj, DESC);
			final Map<String, Integer> map = parseMap(obj, ALLOWED_ENTITIES);
			final Map<String, JsonElement> modules = parseModules(obj);
			
			return new Level(name, modules, mapLoader, mapLocation, desc, map);
		}
		
		private String getStringOrNull(JsonObject obj, String name) {
			return getStringOrNull(obj, name, true);
		}
		
		private String getStringOrNull(JsonObject obj, String name, boolean required) {
			JsonElement element = obj.get(name);
			if (element == null && required)
				throw new JsonParseException("expected an element with name: " + name);
			
			if (element == null || element.isJsonNull())
				return null;
			else
				return element.getAsString();
		}
		
		private Map<String, Integer> parseMap(JsonObject obj, String name) {
			Map<String, Integer> result = new HashMap<>();
			JsonElement element = obj.get(name);
			if (element == null || !element.isJsonObject()) {
				// one robot is the default
				// TODO: for debug purposes this allows 10 robots per connection
				result.put(Robot.class.getName(), 10);
				return result;
			}
			
			JsonObject mapObj = element.getAsJsonObject();
			for (String key : mapObj.keySet()) {
				int value = mapObj.get(key).getAsInt();
				result.put(key, value);
			}
			
			return result;
		}
		
		private Map<String, JsonElement> parseModules(JsonObject obj) {
			Map<String, JsonElement> modules = new HashMap<>();
			JsonElement element = obj.get(MODULES);
			if (element != null && element.isJsonObject()) {
				JsonObject moduleMap = element.getAsJsonObject();
				for (String key : moduleMap.keySet()) {										
					modules.put(key, moduleMap.get(key));
				}
			}
				
			return modules;
		}
	}
}
