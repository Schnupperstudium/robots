package com.github.schnupperstudium.robots.entity;

import com.github.schnupperstudium.robots.server.Game;

public abstract class Item extends Entity {

	public Item(String name) {
		super(name);
	}

	public Item(long uuid, String name) {
		super(name);		
	}
	
	public abstract void use(Game manager, Entity user);
	
	@Override
	public abstract Item clone() throws CloneNotSupportedException;
}
