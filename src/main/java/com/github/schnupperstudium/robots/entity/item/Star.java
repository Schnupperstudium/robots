package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;

public class Star extends Item {
	public static final String ITEM_NAME = "Star";
	
	public Star() {
		super(ITEM_NAME);
	}

	@Override
	public void use(Game manager, Entity user) {
		
	}

	@Override
	public Star clone() throws CloneNotSupportedException {
		return new Star();
	}
}
