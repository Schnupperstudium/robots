package com.github.schnupperstudium.robots.entity;

import java.util.ArrayList;
import java.util.List;

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
}
