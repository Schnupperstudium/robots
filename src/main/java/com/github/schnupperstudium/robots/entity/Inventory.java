package com.github.schnupperstudium.robots.entity;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Serialization;

public class Inventory {
	private final int size;
	private final List<Item> items;
		
	public Inventory(int size) {
		this.size = size;
		this.items = new ArrayList<>(size);
	}
	
	public boolean addItem(Item item) {
		if (item != null && items.size() < size)
			return items.add(item);
		else
			return false;
	}
	
	public Item findItem(long uuid) {
		for (Item item : items ) {
			if (item.getUUID() == uuid)
				return item;
		}
		
		return null;
	}
	
	public boolean removeItem(Item item) {
		return items.remove(item);
	}
	
	public Item removeItem(int index) {
		return items.remove(index);
	}
	
	public int getSize() {
		return size;
	}
	
	public int getUsedSize() {
		return items.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Inventory other = (Inventory) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Inventory [size=" + size + ", items=" + items + "]";
	}
	
	public static final class InventorySerializer extends Serializer<Inventory> {

		public InventorySerializer() {

		}
		
		@Override
		public void write(Kryo kryo, Output output, Inventory inventory) {
			kryo.writeObject(output, inventory.size);
			kryo.writeObject(output, inventory.items.size());
			for (int i = 0; i < inventory.items.size(); i++) {
				kryo.writeClassAndObject(output, inventory.items.get(i));
			}
		}

		@Override
		public Inventory read(Kryo kryo, Input input, Class<Inventory> type) {
			int size = kryo.readObject(input, int.class);
			int containedItems = kryo.readObject(input, int.class);

			Inventory inventory = new Inventory(size);
			for (int i = 0; i < containedItems; i++) {
				Item item = (Item) kryo.readClassAndObject(input);
				inventory.addItem(item);
			}
			
			return inventory;
		}
		
	}
}
