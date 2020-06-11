package de.nerogar.noise.oldGame;

import de.nerogar.noise.oldGame.core.systems.GameObjectsSystem;
import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;

public abstract class Component<T extends Component<T>> {

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

	public abstract T newInstance();

	public abstract void copyFrom(T other);

	public final Component copy() {
		T t = (T) this;
		T newInstance = newInstance();
		newInstance.copyFrom(t);
		return newInstance;
	}

}
