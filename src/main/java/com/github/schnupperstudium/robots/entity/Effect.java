package com.github.schnupperstudium.robots.entity;

public abstract class Effect {
	private final String name;	
	
	public Effect(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return name;
	}
}
