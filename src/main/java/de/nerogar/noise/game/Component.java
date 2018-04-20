package de.nerogar.noise.game;

import de.nerogar.noise.game.core.systems.GameObjectsSystem;
import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;

public abstract class Component implements Cloneable {

	private Entity entity;

	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
	}

	public void save(NDSNodeObject data) throws NDSException {
	}

	protected final void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() { return entity; }

	protected void initSystems() {
	}

	protected void init() {
	}

	protected void cleanup() {
	}

	@Override
	public abstract Component clone();

}
