package com.github.schnupperstudium.robots.network.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;

public class EntitySerializer<T extends Entity> extends Serializer<T> {

	public EntitySerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T entity) {
		output.writeLong(entity.getUUID());
		kryo.writeObject(output, entity.getName());
		kryo.writeObject(output, entity.getFacing());
		output.writeInt(entity.getX());
		output.writeInt(entity.getY());		
	}

	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		long uuid = input.readLong();
		String name = kryo.readObject(input, String.class);
		Facing facing = kryo.readObject(input, Facing.class);
		int x = input.readInt();
		int y = input.readInt();
		
		return create(type, uuid, name, facing, x, y);
	}

	protected final T create(Class<T> type, long uuid, String name, Facing facing, int x, int y) {
		try {
			Constructor<T> constructor = type.getConstructor(long.class, String.class, Facing.class, int.class, int.class);
			return constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return create(type);
		}
	}
	
	protected final T create(Class<T> type) {
		try {
			Constructor<T> constructor = type.getConstructor();
			return constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new KryoException("could not find constructor for: " + type.getName());
		}
	}
}
