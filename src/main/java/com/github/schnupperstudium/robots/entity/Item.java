package com.github.schnupperstudium.robots.entity;

import com.github.schnupperstudium.robots.server.GameManager;

public abstract class Item extends Entity {

	public Item(String name) {
		super(name);
	}

	public Item(long uuid, String name) {
		super(name);		
	}
	
	public abstract void use(GameManager manager, Entity user);
}
