package com.github.schnupperstudium.robots.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class ThrowableSerializer extends Serializer<Throwable> {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void write(final Kryo kryo, final Output output, final Throwable object) {
		kryo.writeObjectOrNull(output, object.getMessage(), String.class);
		LOGGER.debug("Exception transmitted to client.", object);
	}

	@Override
	public Throwable read(final Kryo kryo, final Input input, final Class<Throwable> type) {
		try {
			Throwable t = type.getDeclaredConstructor().newInstance();
			Field detailMessage = Throwable.class.getDeclaredField("detailMessage");
			detailMessage.setAccessible(true);
			detailMessage.set(t, kryo.readObjectOrNull(input, String.class));
			return t;
		} catch (Exception e) {
			LOGGER.catching(e);
			return null;
		}
	}

}
