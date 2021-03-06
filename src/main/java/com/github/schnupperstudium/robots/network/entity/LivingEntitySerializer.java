package com.github.schnupperstudium.robots.network.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.LivingEntity;

public class LivingEntitySerializer<T extends LivingEntity> extends EntitySerializer<T> {
	
	public LivingEntitySerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T entity) {
		super.write(kryo, output, entity);
		
		output.writeInt(entity.getCurrentHealth());
		output.writeInt(entity.getMaxHealth());
	}
	
	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		long uuid = input.readLong();
		String name = kryo.readObject(input, String.class);
		Inventory inventory = (Inventory) kryo.readClassAndObject(input);
		Facing facing = kryo.readObject(input, Facing.class);
		int x = input.readInt();
		int y = input.readInt();
		int currentHealth = input.readInt();
		int maxHealth = input.readInt();
		
		return create(type, uuid, name, inventory, facing, x, y, currentHealth, maxHealth);
	}
	
	protected final T create(Class<T> type, long uuid, String name, Inventory inventory, Facing facing, int x, int y, int currentHealth, int maxHealth) {
		try {
			Constructor<T> constructor = type.getConstructor(long.class, String.class, Inventory.class, Facing.class, int.class, int.class, int.class, int.class);
			return constructor.newInstance(uuid, name, inventory, facing, x, y, currentHealth, maxHealth);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new KryoException("could not find constructor for: " + type.getName());
		}
	}
}
