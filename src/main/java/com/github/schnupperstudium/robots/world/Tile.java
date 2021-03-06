package com.github.schnupperstudium.robots.world;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.gui.overlay.TileRenderAddition;

/**
 * Represents a tile in a {@link World}.
 * 
 * @author Daniel Wieland
 *
 */
public class Tile {
	/** world (<code>null</code> on client). */
	private transient final World world;
	/** additional render operations on tiles (<code>null</code> on server). */
	private transient final List<TileRenderAddition> tileRenderAdditions = new ArrayList<>();	
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
	
	/**
	 * Checks if the current material for this tile can be walked on. 
	 * The tile can sill be blocked by an entity even if this returns <code>true</code>.
	 * 
	 * @return true if an entity can walk on this tile. 
	 */
	public boolean isVisitable() {
		return material.isVisitable();
	}

	/**
	 * @return true if there are render additions present for this tile.
	 */
	public boolean hasTileRenderAdditions() {
		return !tileRenderAdditions.isEmpty();
	}
	
	/**
	 * @return a copy of the list for additional renderers.
	 */
	public List<TileRenderAddition> getTileRenderAdditions() {
		return new ArrayList<>(tileRenderAdditions);
	}
	
	/**
	 * @param renderAddition new render addition.
	 */
	public void addTileRenderAddition(TileRenderAddition renderAddition) {
		if (renderAddition != null)
			tileRenderAdditions.add(renderAddition);
	}
	
	/**
	 * Removes all render additions.
	 */
	public void clearTileRenderAdditions() {
		tileRenderAdditions.clear();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Tile other = (Tile) obj;

		return other.x == this.x && other.y == this.y;
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
