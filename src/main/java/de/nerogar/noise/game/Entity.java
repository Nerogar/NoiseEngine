package de.nerogar.noise.game;

import de.nerogar.noiseInterface.game.IComponent;
import de.nerogar.noiseInterface.game.IEntity;

public class Entity implements IEntity {

	private final int             id;
	private final EntityContainer entityContainer;

	protected Entity(int id, EntityContainer entityContainer) {
		this.id = id;
		this.entityContainer = entityContainer;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public <T extends IComponent> T getComponent(Class<T> componentClass) {
		return entityContainer.getEntityComponent(id, componentClass);
	}

	@Override
	public IComponent[] getComponents() {
		return entityContainer.getComponents(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Entity)) return false;
		Entity entity = (Entity) o;
		return id == entity.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
