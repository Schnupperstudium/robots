package com.github.schnupperstudium.robots.entity.item;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.server.Game;

public class Cookie extends Item {
	public static final String ITEM_NAME = "Cookie";
	
	public Cookie() {
		super(ITEM_NAME);
	}

	public Cookie(long uuid, Facing facing, int x, int y) {
		super(uuid, ITEM_NAME, facing, x, y);
	}
	
	@Override
	public void use(Game manager, Entity user) {
		user.setName("CookieMonster");
	}

	@Override
	public Cookie clone() throws CloneNotSupportedException {
		return new Cookie(getUUID(), getFacing(), getX(), getY());
	}
}
