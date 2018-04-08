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
}
