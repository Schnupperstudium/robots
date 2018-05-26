package com.github.schnupperstudium.robots.world;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;

public class Tile {
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
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		if (world != null && this.material != material) {
			if (material == Material.SPAWN)
				world.addSpawn(this);
			else if (this.material == Material.SPAWN)
				world.removeSpawn(this);
		}
		
		this.material = material;
	}
	
	public boolean hasItem() {
		return item != null;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
		if (item != null)
			item.setPosition(x, y);
	}
	
	public boolean hasVisitor() {
		return visitor != null;
	}
	
	public Entity getVisitor() {
		return visitor;
	}
	
	public void setVisitor(Entity visitor) {
		this.visitor = visitor;
		if (visitor != null)
			visitor.setPosition(x, y);
	}
	
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
