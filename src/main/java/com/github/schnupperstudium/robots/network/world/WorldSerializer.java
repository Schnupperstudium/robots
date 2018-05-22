package com.github.schnupperstudium.robots.network.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class WorldSerializer<T extends World> extends Serializer<T> {

	public WorldSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T world) {
		final int widht = world.getWidth();
		final int height = world.getHeight();
		output.writeInt(widht);
		output.writeInt(height);
		
		for (int x = 0; x < widht; x++) {
			for (int y = 0; y < height; y++) {
				kryo.writeClassAndObject(output, world.getTile(x, y));
			}
		}
	}

	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		int width = input.readInt();
		int height = input.readInt();
		int tileCount = width * height;
		List<Tile> tiles = new ArrayList<>(tileCount);
		for (int i = 0; i < tileCount; i++) {
			Tile tile = (Tile) kryo.readClassAndObject(input);
			tiles.add(tile);
		}
		
		return create(type, width, height, tiles);
	}

	protected final T create(Class<T> type, int width, int height, Collection<Tile> tiles) {
		try {
			Constructor<T> constructor = type.getConstructor(int.class, int.class, Collection.class);
			return constructor.newInstance(width, height, tiles);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new KryoException("could not find constrcutor for class: " + type.getName());
		}
	}
}
