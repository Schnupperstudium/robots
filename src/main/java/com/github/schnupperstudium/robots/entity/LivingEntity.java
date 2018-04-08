package com.github.schnupperstudium.robots.entity;

public abstract class LivingEntity extends Entity {
	private int maxHealth;
	private int currentHealth;
	
	public LivingEntity(String name, int health) {
		super(name);
		
		this.maxHealth = health;
		this.currentHealth = health;
	}

	public LivingEntity(long uuid, String name, int health) {
		super(uuid, name);
		
		this.maxHealth = health;
		this.currentHealth = health;
	}
	
	public void heal(int heal) {
		int health = currentHealth + heal;
		currentHealth = Math.min(maxHealth, health);
	}
	
	public void damage(int damage) {
		int health = currentHealth - damage;
		currentHealth = Math.min(maxHealth, health);
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}
	
	public boolean isAlive() {
		return currentHealth > 0;
	}
	
	public boolean isDead() {
		return currentHealth <= 0;
	}
}
