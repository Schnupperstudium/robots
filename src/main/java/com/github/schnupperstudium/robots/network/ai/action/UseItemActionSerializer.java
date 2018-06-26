package com.github.schnupperstudium.robots.network.ai.action;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.ai.action.UseItemAction;

public class UseItemActionSerializer extends Serializer<UseItemAction> {

	public UseItemActionSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, UseItemAction action) {
		output.writeLong(action.getItemUUID());
	}

	@Override
	public UseItemAction read(Kryo kryo, Input input, Class<UseItemAction> type) {
		long uuid = input.readLong();
		
		return new UseItemAction(uuid);
	}

}
