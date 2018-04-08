package com.github.schnupperstudium.robots.module.ai;

import java.util.List;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.module.ai.action.EntityAction;
import com.github.schnupperstudium.robots.world.Field;

public interface AIClientModule {
	void updateEntity(long uuid, Entity entity);
	void updateVisableFields(long uuid, List<Field> fields);
	EntityAction makeTurn(long uuid);
}
