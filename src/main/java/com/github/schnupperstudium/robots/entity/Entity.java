package com.github.schnupperstudium.robots.entity;

import java.util.ArrayList;
import java.util.List;

import com.github.schnupperstudium.robots.UUIDGenerator;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

/**
 * This is a basic entity for games. It stores the position, facing and some other essential properties.
 * 
 * @author Daniel Wieland
 *
 */
public abstract class Entity {
	private static final Facing DEFAULT_FACING = Facing.NORTH;
	
	private final long uuid;
	private final List<Effect> effects = new ArrayList<>();
	
	private transient World world;
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
	
	/**
	 * @return unique identifier for this entity.
	 */
	public final long getUUID() {
		return uuid;
	}
	
	/**
	 * Returns the world this client is spawned in. 
	 * This field will be <code>null</code> on the client. 
	 * 
	 * @return world or <code>null</code> on client.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Changes the current world.
	 * 
	 * @param world world.
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * adds an {@link Effect} to this entity.
	 * 
	 * @param e an effect
	 */
	public void addEffect(Effect e) {
		synchronized (effects) {
			if (e != null)
				effects.add(e);
		}
	}
	
	/**
	 * searches for an effect of the given type.
	 * 
	 * @param effectClass searched class.
	 * @return found effect or <code>null</code>
	 */
	public <T extends Effect> T getEffect(Class<T> effectClass) {
		synchronized (effects) {
			for (Effect e : effects) {
				if (effectClass.equals(e.getClass()))
					return effectClass.cast(e);
			}
		}
		
		return null;
	}
	
	/**
	 * removes a given effect.
	 * 
	 * @param e effect to remove.
	 */
	public void removeEffect(Effect e) {
		synchronized (effects) {
			effects.remove(e);
		}
	}
	
	public Tile getTile(World world) {
		return world.getTile(x, y);
	}
	
	/**
	 * @return name of this entity.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * changes the name for this entity.
	 * 
	 * @param name new name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the inventory of this entity or <code>null</code>
	 */
	public Inventory getInventory() {
		return inventory;
	}
	
	/**
	 * @return true if this entity has an inventory.
	 */
	public boolean hasInventory() {
		return inventory != null;
	}
	
	/**
	 * assigns a new inventory to this entity.
	 * 
	 * @param inventory new inventory.
	 */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	/**
	 * @return facing of this entity.
	 */
	public Facing getFacing() {
		return facing;
	}
	
	/**
	 * changes the facing of this entity.
	 * 
	 * @param facing new facing.
	 */
	public void setFacing(Facing facing) {
		this.facing = facing;
	}
	
	/**
	 * @return x coordinate.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * set x coordinate.
	 * 
	 * @param x new x coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * @return y coordinate.
	 */
	public int getY() {
		return y;
	}
	
	
	/**
	 * set y coordinate.
	 * 
	 * @param y new y coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * sets the x and y coordinate.
	 * 
	 * @param x new x coordinate.
	 * @param y new y coordinate.
	 */
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
