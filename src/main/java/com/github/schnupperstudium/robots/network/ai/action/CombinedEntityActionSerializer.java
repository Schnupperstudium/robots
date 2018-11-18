package com.github.schnupperstudium.robots.network.ai.action;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.ai.action.CombinedEntityAction;
import com.github.schnupperstudium.robots.ai.action.EntityAction;

public class CombinedEntityActionSerializer extends Serializer<CombinedEntityAction> {

	public CombinedEntityActionSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, CombinedEntityAction combinedAction) {
		final List<EntityAction> actions = combinedAction.getActions();
		output.writeInt(actions.size());
		for (EntityAction action : actions) {
			kryo.writeClassAndObject(output, action);
		}
	}

	@Override
	public CombinedEntityAction read(Kryo kryo, Input input, Class<CombinedEntityAction> type) {
		final int size = input.readInt();
		EntityAction[] actions = new EntityAction[size];
		for (int i = 0; i < size; i++) {
			actions[i] = (EntityAction) kryo.readClassAndObject(input);
		}
		
		return new CombinedEntityAction(actions);
	}
}
