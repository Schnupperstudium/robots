package com.github.schnupperstudium.robots.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Item;

public class InventorySerializer<T extends Inventory> extends Serializer<T> {

	public InventorySerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T inventory) {
		output.writeInt(inventory.getSize());
		output.writeInt(inventory.getUsedSize());
		List<Item> items = inventory.getItems();
		for (Item item : items) {
			kryo.writeClassAndObject(output, item);
		}
	}

	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		int size = input.readInt();
		int usedSize = input.readInt();
		List<Item> items = new ArrayList<>(usedSize);
		for (int i = 0; i < usedSize; i++) {
			items.add((Item) kryo.readClassAndObject(input));
		}
		
		return create(type, size, items);
	}

	protected final T create(Class<T> type, int size, List<Item> items) {
		try {
			Constructor<T> constructor = type.getConstructor(int.class, List.class);
			return constructor.newInstance(size, items);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new KryoException("constructor for class not found: " + type.getName());
		}
	}
}
