package com.github.schnupperstudium.robots.network.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Robot;

public class RobotSerializer<T extends Robot> extends LivingEntitySerializer<T> {
	
	public RobotSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T entity) {
		super.write(kryo, output, entity);
		
		kryo.writeClassAndObject(output, entity.getInventory());
	}
	
	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		long uuid = input.readLong();
		String name = kryo.readObject(input, String.class);
		Facing facing = kryo.readObject(input, Facing.class);
		int x = input.readInt();
		int y = input.readInt();
		int currentHealth = input.readInt();
		int maxHealth = input.readInt();
		Inventory inventory = (Inventory) kryo.readClassAndObject(input);
		
		return create(type, uuid, name, facing, x, y, currentHealth, maxHealth, inventory);
	}
	
	protected final T create(Class<T> type, long uuid, String name, Facing facing, int x, int y, int currentHealth, int maxHealth, Inventory inventory) {
		try {
			Constructor<T> constructor = type.getConstructor(long.class, String.class, Facing.class, int.class, int.class, int.class, int.class, Inventory.class);
			return constructor.newInstance(uuid, name, facing, x, y, currentHealth, maxHealth, inventory);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return create(type);
		}
	}
}
