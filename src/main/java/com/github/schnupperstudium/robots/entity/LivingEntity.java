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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentHealth;
		result = prime * result + maxHealth;
		return result + super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof LivingEntity))
			return false;
		
		LivingEntity other = (LivingEntity) obj;
		if (currentHealth != other.currentHealth)
			return false;
		if (maxHealth != other.maxHealth)
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "LivingEntity [maxHealth=" + maxHealth + ", currentHealth=" + currentHealth + ", getUUID()=" + getUUID()
				+ ", getName()=" + getName() + ", getX()=" + getX() + ", getY()=" + getY() + ", getClass()="
				+ getClass() + "]";
	}
}
