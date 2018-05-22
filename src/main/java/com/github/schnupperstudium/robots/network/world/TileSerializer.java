package com.github.schnupperstudium.robots.network.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.Tile;

public class TileSerializer<T extends Tile> extends Serializer<T> {

	public TileSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T tile) {
		output.writeInt(tile.getX());
		output.writeInt(tile.getY());
		kryo.writeClassAndObject(output, tile.getMaterial());
		kryo.writeClassAndObject(output, tile.getVisitor());
		kryo.writeClassAndObject(output, tile.getItem());
	}

	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		int x = input.readInt();
		int y = input.readInt();
		Material material = (Material) kryo.readClassAndObject(input);
		Entity visitor = (Entity) kryo.readClassAndObject(input);
		Item item = (Item) kryo.readClassAndObject(input);
		
		return create(type, x, y, material, visitor, item);
	}

	protected final T create(Class<T> type, int x, int y, Material material, Entity visitor, Item item) {
		try {
			Constructor<T> constructor = type.getConstructor(int.class, int.class, Material.class, Entity.class, Item.class);
			return constructor.newInstance(x, y, material, visitor, item);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new KryoException("could not find constrcutor for class: " + type.getName());
		}
	}
}
