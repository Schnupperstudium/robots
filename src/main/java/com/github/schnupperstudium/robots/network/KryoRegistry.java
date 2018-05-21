package com.github.schnupperstudium.robots.network;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.RobotsServerInterface;
import com.github.schnupperstudium.robots.world.Tile;
import com.github.schnupperstudium.robots.world.World;

public class KryoRegistry {

	/**
	 * Utility class
	 */
	private KryoRegistry() {
	}

	public static final void registerClasses(final Kryo kryo) {
		ObjectSpace.registerClasses(kryo);

		kryo.setRegistrationRequired(false);

		kryo.addDefaultSerializer(Throwable.class, new ThrowableSerializer());
		kryo.addDefaultSerializer(World.class, new World.WorldSerializer());
		kryo.addDefaultSerializer(Tile.class, new Tile.TileSerializer());
		kryo.addDefaultSerializer(Inventory.class, new Inventory.InventorySerializer());
		
		kryo.register(LinkedList.class);
		kryo.register(ArrayList.class);
		kryo.register(GameInfo.class);
		kryo.register(Level.class);
		kryo.register(RobotsClientInterface.class);
		kryo.register(RobotsServerInterface.class);
		kryo.register(Entity.class);
		kryo.register(World.class);
		kryo.register(Tile.class);
//		kryo.register(Field.class, new FieldSerializer());
//		kryo.addDefaultSerializer(Robot.class, RobotSerializer.class);
//		kryo.addDefaultSerializer(LivingEntity.class, LivingEntitySerializer.class);
//		kryo.addDefaultSerializer(Entity.class, EntitySerializer.class);
//		kryo.addDefaultSerializer(Inventory.class, InventorySerializer.class);
//		kryo.addDefaultSerializer(Weapon.class, WeaponSerializer.class);
//		kryo.addDefaultSerializer(Gun.class, GunSerializer.class);
	}
}
