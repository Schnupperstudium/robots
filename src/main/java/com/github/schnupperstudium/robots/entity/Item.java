package com.github.schnupperstudium.robots.entity;

import com.github.schnupperstudium.robots.server.Game;

public abstract class Item extends Entity {

	public Item(String name) {
		super(name);
	}

	public Item(long uuid, String name) {
		super(uuid, name);		
	}
	
	public Item(long uuid, String name, Facing facing, int x, int y) {
		super(uuid, name, facing, x, y);
	}
	
	public abstract void use(Game game, Entity entity);
	
	@Override
	public abstract Item clone() throws CloneNotSupportedException;
}
