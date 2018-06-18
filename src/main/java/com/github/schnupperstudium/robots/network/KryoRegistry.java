package com.github.schnupperstudium.robots.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.github.schnupperstudium.robots.ai.action.DropItemAction;
import com.github.schnupperstudium.robots.ai.action.MoveBackwardAction;
import com.github.schnupperstudium.robots.ai.action.MoveForwardAction;
import com.github.schnupperstudium.robots.ai.action.NoAction;
import com.github.schnupperstudium.robots.ai.action.PickUpItemAction;
import com.github.schnupperstudium.robots.ai.action.TurnLeftAction;
import com.github.schnupperstudium.robots.ai.action.TurnRightAction;
import com.github.schnupperstudium.robots.client.RobotsClientInterface;
import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Facing;
import com.github.schnupperstudium.robots.entity.Inventory;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.entity.LivingEntity;
import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.entity.item.Star;
import com.github.schnupperstudium.robots.network.ai.action.DropItemActionSerializer;
import com.github.schnupperstudium.robots.network.entity.EntitySerializer;
import com.github.schnupperstudium.robots.network.entity.LivingEntitySerializer;
import com.github.schnupperstudium.robots.network.entity.RobotSerializer;
import com.github.schnupperstudium.robots.network.item.ItemSerializer;
import com.github.schnupperstudium.robots.network.world.TileSerializer;
import com.github.schnupperstudium.robots.network.world.WorldSerializer;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.RobotsServerInterface;
import com.github.schnupperstudium.robots.world.Material;
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

		kryo.setRegistrationRequired(true);
		
		// Java
		kryo.register(Throwable.class, new ThrowableSerializer());
//		kryo.register(AssertionFailedError.class, new ThrowableSerializer());
		kryo.register(IllegalStateException.class, new ThrowableSerializer());
		kryo.register(NullPointerException.class, new ThrowableSerializer());
		kryo.register(LinkedList.class);
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);

		// General
		kryo.register(GameInfo.class);
		kryo.register(Level.class);
		kryo.register(Facing.class);
		kryo.register(Material.class);
		kryo.register(RobotsClientInterface.class);
		kryo.register(RobotsServerInterface.class);
		
		// AI
		kryo.register(PickUpItemAction.class);
		kryo.register(DropItemAction.class, new DropItemActionSerializer());
		kryo.register(MoveBackwardAction.class);
		kryo.register(MoveForwardAction.class);
		kryo.register(TurnLeftAction.class);
		kryo.register(TurnRightAction.class);
		kryo.register(NoAction.class);		
		
		// World		
		kryo.register(World.class, new WorldSerializer<>());
		kryo.register(Tile.class, new TileSerializer<>());
		
		// Entity
		kryo.register(Entity.class, new EntitySerializer<>());
		kryo.register(LivingEntity.class, new LivingEntitySerializer<>());
		kryo.register(Robot.class, new RobotSerializer<>());
		
		// Items & Inventory
		kryo.register(Inventory.class, new InventorySerializer<>());
		kryo.register(Item.class, new ItemSerializer<>());
		kryo.register(Star.class);
		
		// ******************
		// *** SERIALIZER ***
		// ******************
		
		// Java
		kryo.addDefaultSerializer(Throwable.class, new ThrowableSerializer());
	}
}
