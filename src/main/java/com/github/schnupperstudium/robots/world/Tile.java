package com.github.schnupperstudium.robots.world;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
		if (item != null)
			item.setPosition(x, y);
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
	public String toString() {
		return "Tile [x=" + x + ", y=" + y + ", material=" + material + ", visitor=" + visitor + ", item=" + item + "]";
	}
	
	public static final class TileSerializer extends Serializer<Tile> {

		public TileSerializer() {

		}
		
		@Override
		public void write(Kryo kryo, Output output, Tile tile) {
			kryo.writeObject(output, tile.x);
			kryo.writeObject(output, tile.y);
			kryo.writeObjectOrNull(output, tile.material, Material.class);
			kryo.writeClassAndObject(output, tile.visitor);
			kryo.writeClassAndObject(output, tile.item);
		}

		@Override
		public Tile read(Kryo kryo, Input input, Class<Tile> type) {
			int x = kryo.readObject(input, int.class);
			int y = kryo.readObject(input, int.class);
			Material material = kryo.readObjectOrNull(input, Material.class);
			Entity visitor = (Entity) kryo.readClassAndObject(input);
			Item item = (Item) kryo.readClassAndObject(input);
			
			Tile tile = new Tile(null, x, y, material);
			tile.setVisitor(visitor);
			tile.setItem(item);
			
			return tile;
		}

	}
}
