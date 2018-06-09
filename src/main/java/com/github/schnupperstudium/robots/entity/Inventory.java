package com.github.schnupperstudium.robots.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inventory.
 * 
 * @author Daniel Wieland
 */
public class Inventory {
	private final int size;
	private final List<Item> items;
		
	public Inventory(int size) {
		this.size = size;
		this.items = new ArrayList<>(size);
	}
	
	public Inventory(int size, List<Item> items) {
		if (items == null)
			throw new IllegalArgumentException("items was null");
		if (items.size() > size)
			throw new IllegalArgumentException("list contains more items than allowed by inventory size");
		
		this.size = size;
		this.items = new ArrayList<>(items);
	}
	
	/**
	 * Adds the item to this inventory, if possible.
	 * 
	 * @param item item to be added.
	 * @return true if it was added successfully.
	 */
	public boolean addItem(Item item) {
		if (item != null && items.size() < size)
			return items.add(item);
		else
			return false;
	}
	
	/**
	 * Attempts to find an item contained in this inventory with the given uuid.
	 * 
	 * @param uuid uuid of the searched item.
	 * @return item with matching uuid or <code>null</code>.
	 */
	public Item findItem(long uuid) {
		for (Item item : items ) {
			if (item.getUUID() == uuid)
				return item;
		}
		
		return null;
	}
	
	/**
	 * @return a copy of the list containing all items in this inventory.
	 */
	public List<Item> getItems() {
		return new ArrayList<>(items);
	}
	
	/**
	 * Removes the given Item.
	 * 
	 * @param item item to be removed.
	 * @return true if it was removed, false otherwise.
	 */
	public boolean removeItem(Item item) {
		return items.remove(item);
	}
	
	/**
	 * @return number of available slots in this inventory.
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * @return number of used slots
	 */
	public int getUsedSize() {
		return items.size();
	}

	/**
	 * @return number of free slots.
	 */
	public int getFreeSlots() {
		return getSize() - getUsedSize();
	}
	
	@Override
	public Inventory clone() throws CloneNotSupportedException {
		List<Item> itemsClone = new ArrayList<>(size);
		for (Item item : items) 
			itemsClone.add(item.clone());
		
		return new Inventory(size, itemsClone);
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
