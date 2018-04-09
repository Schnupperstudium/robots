package com.github.schnupperstudium.robots.entity;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public abstract class Entity {
	private final long uuid;
	private final List<Effect> effects = new ArrayList<>();
	
	private String name;
	private int x;
	private int y;
	
	public Entity(String name) {
		this(UUIDGenerator.obtain(), name);
	}
	
	public Entity(long uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	public final long getUUID() {
		return uuid;
	}
	
	public void addEffect(Effect e) {
		synchronized (effects) {
			if (e != null)
				effects.add(e);
		}
	}
	
	public <T extends Effect> T getEffect(Class<T> effectClass) {
		synchronized (effects) {
			for (Effect e : effects) {
				if (effectClass.equals(e.getClass()))
					return effectClass.cast(e);
			}
		}
		
		return null;
	}
	
	public void removeEffect(Effect e) {
		synchronized (effects) {
			effects.remove(e);
		}
	}
	
	public Tile getTile(World world) {
		return world.getField(x, y);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}
}
