package com.github.schnupperstudium.robots.network.ai.action;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.ai.action.DropItemAction;

public class DropItemActionSerializer extends Serializer<DropItemAction> {

	public DropItemActionSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, DropItemAction action) {
		output.writeLong(action.getUUID());
	}

	@Override
	public DropItemAction read(Kryo kryo, Input input, Class<DropItemAction> type) {
		long uuid = input.readLong();
		
		return new DropItemAction(uuid);
	}

}
