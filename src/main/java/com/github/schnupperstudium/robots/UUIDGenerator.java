package com.github.schnupperstudium.robots;

import java.util.concurrent.atomic.AtomicLong;

public final class UUIDGenerator {
	private static final AtomicLong COUNTER = new AtomicLong(1);
	
	private UUIDGenerator() {
		
	}
	
	public static long obtain() {
		return COUNTER.getAndIncrement();
	}
	
	public static boolean isValid(long uuid) {
		return uuid > 0;
	}
}
