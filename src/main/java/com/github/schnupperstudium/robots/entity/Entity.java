package com.github.schnupperstudium.robots.entity;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public abstract class Entity {
	private static final Facing DEFAULT_FACING = Facing.NORTH;
	
	private final long uuid;
	private final List<Effect> effects = new ArrayList<>();
	
	private String name;	
	private Inventory inventory;
	private Facing facing;	
	private int x;
	private int y;
		
	public Entity(String name) {
		this(UUIDGenerator.obtain(), name);
	}
	
	public Entity(long uuid, String name) {
		this(uuid, name, DEFAULT_FACING, 0, 0);
	}
	
	public Entity(long uuid, String name, Facing facing, int x, int y) {
		this(uuid, name, null, facing, x, y);
	}
	
	public Entity(long uuid, String name, Inventory inventory, Facing facing, int x, int y) {
		this.uuid = uuid;
		this.name = name;
		this.inventory = inventory;
		this.facing = facing;
		this.x = x;
		this.y = y;
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
		return world.getTile(x, y);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public boolean hasInventory() {
		return inventory != null;
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public Facing getFacing() {
		return facing;
	}
	
	public void setFacing(Facing facing) {
		this.facing = facing;
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

	@Override
	public abstract Entity clone() throws CloneNotSupportedException;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (uuid ^ (uuid >>> 32));
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
		
		if (!(obj instanceof Entity))
			return false;

		Entity other = (Entity) obj;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uuid != other.uuid)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		
		return true;
	}
}
