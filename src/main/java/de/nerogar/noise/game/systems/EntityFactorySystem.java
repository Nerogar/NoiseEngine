package de.nerogar.noise.game.systems;

import de.nerogar.noise.game.entityFactories.IEntityFactory;
import de.nerogar.noiseInterface.game.IGameSystem;

import java.util.HashMap;
import java.util.Map;

public class EntityFactorySystem implements IGameSystem {

	private Map<Short, IEntityFactory> entityFactories;

	public EntityFactorySystem() {
		this.entityFactories = new HashMap<>();
	}

	public void addEntityFactory(short entityTypeId, IEntityFactory entityFactory) {
		entityFactories.put(entityTypeId, entityFactory);
	}

	@SuppressWarnings("unchecked")
	private <T extends IEntityFactory> T getEntityFactory(short entityTypeId, Class<T> entityFactoryClass) {
		return (T) entityFactories.get(entityTypeId);
	}

	public IEntityFactory getEntityFactory(short entityTypeId) {
		return entityFactories.get(entityTypeId);
	}

}
