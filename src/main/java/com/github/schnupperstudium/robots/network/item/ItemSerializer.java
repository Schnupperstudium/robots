package com.github.schnupperstudium.robots.network.item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.esotericsoftware.kryo.KryoException;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.network.entity.EntitySerializer;

public class ItemSerializer<T extends Item> extends EntitySerializer<T> {
	
	public ItemSerializer() {

	}

	@Override
	protected T create(Class<T> type, long uuid, String name, Inventory inventory, Facing facing, int x, int y) {
		try {
			Constructor<T> constructor = type.getConstructor(long.class, Facing.class, int.class, int.class);
			return constructor.newInstance(uuid, facing, x, y);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new KryoException("could not find constructor for: " + type.getName());
		}
	}
}
