package com.github.schnupperstudium.robots.network.entity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.schnupperstudium.robots.entity.Robot;

public class RobotSerializer<T extends Robot> extends LivingEntitySerializer<T> {
	
	public RobotSerializer() {

	}
	
	@Override
	public void write(Kryo kryo, Output output, T entity) {
		super.write(kryo, output, entity);
	}
	
	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		// there are no new attirbutes
		return super.read(kryo, input, type);
	}
}
