package com.github.schnupperstudium.robots.world;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;

public class Field {
	private final int x;
	private final int y;
		
	private Material material;
	private Entity visitor;
	private Item item;

	public Field(int x, int y) {
		this(x, y, Material.VOID);
	}
	
	public Field(int x, int y, Material material) {
		this.x = x;
		this.y = y;
		this.material = material;
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
		this.material = material;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Entity getVisitor() {
		return visitor;
	}
	
	public void setVisitor(Entity visitor) {
		this.visitor = visitor;
	}
	
	public boolean canVisit() {
		return material.isVisitable() && visitor != null;
	}
}
