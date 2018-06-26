package com.github.schnupperstudium.robots.world;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;

/**
 * Represents a tile in a {@link World}.
 * 
 * @author Daniel Wieland
 *
 */
public class Tile {
	/** world (<code>null</code> on client). */
	private transient final World world;
	private final int x;
	private final int y;
		
	private Material material;
	private Entity visitor;
	private Item item;

	public Tile(World world, Tile other) {
		this.x = other.x;
		this.y = other.y;
		this.world = world;
		
		setMaterial(other.material);
		setVisitor(other.visitor);
		setItem(other.item);
	}
	
	public Tile(World world, int x, int y) {
		this(world, x, y, Material.VOID);
	}
	
	public Tile(World world, int x, int y, Material material) {
		this.world = world;
		this.x = x;
		this.y = y;
		setMaterial(material);
	}
	
	public Tile(int x, int y, Material material, Entity visitor, Item item) {
		this.world = null;
		this.x = x;
		this.y = y;
		setMaterial(material);
		setVisitor(visitor);
		setItem(item);
	}
	
	/**
	 * @return x coordinate.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return y coordinate.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * @return material of this tile.
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * changes the material of this tile.
	 * 
	 * @param material new material.
	 */
	public void setMaterial(Material material) {
		if (world != null && this.material != material) {
			if (material == Material.SPAWN)
				world.addSpawn(this);
			else if (this.material == Material.SPAWN)
				world.removeSpawn(this);
		}
		
		this.material = material;
	}
	
	/**
	 * @return true if there is an item on this tile.
	 */
	public boolean hasItem() {
		return item != null;
	}
	
	/**
	 * @return item on this tile or <code>null</code>
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * sets the item on this tile.
	 *  
	 * @param item new item on this tile.
	 */
	public void setItem(Item item) {
		this.item = item;
		if (item != null)
			item.setPosition(x, y);
	}
	
	/**
	 * @return true if there is an entity on this tile.
	 */
	public boolean hasVisitor() {
		return visitor != null;
	}
	
	/**
	 * @return entity on this tile or <code>null</code>
	 */
	public Entity getVisitor() {
		return visitor;
	}
	
	/**
	 * changes the visitor for this tile.
	 * 
	 * @param visitor new visitor.
	 */
	public void setVisitor(Entity visitor) {
		this.visitor = visitor;
		if (visitor != null)
			visitor.setPosition(x, y);
	}
	
	/**
	 * Removes the visitor from this filed if the uuid matches 
	 * with the given visitor.
	 * 
	 * @param entity visitor has to match entities uuid to be removed.
	 * @return true if the visitor was set to null.
	 */
	public boolean clearVisitor(Entity entity) {
		if (entity == null || visitor == null)
			return visitor == null;
		
		if (visitor.getUUID() == entity.getUUID()) {
			visitor = null;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return true if an entity can walk on this tile <b>and</b> there is no visitor currently occupying this tile.
	 */
	public boolean canVisit() {
		return material.isVisitable() && visitor == null;
	}

	@Override
	public Tile clone() throws CloneNotSupportedException {
		return new Tile(x, y, material, visitor != null ? visitor.clone() : null, item != null ? item.clone() : null);
	}
	
	@Override
	public String toString() {
		return "Tile [x=" + x + ", y=" + y + ", material=" + material + ", visitor=" + visitor + ", item=" + item + "]";
	}
}
