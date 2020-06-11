package de.nerogar.noise.oldGame;

import de.nerogar.noise.oldGame.core.systems.EntityFactorySystem;
import de.nerogar.noise.network.INetworkAdapter;

import java.util.Collection;

public class Entity {

	private final EntityList entityList;

	private final short               entityID;
	private final int                 id;
	private final EntityFactorySystem entityFactory;
	private final CoreMap             map;

	private INetworkAdapter networkConnection;

	public Entity(short entityID, int id, EntityFactorySystem entityFactory, CoreMap map) {
		if (map != null) {
			this.entityList = map.getEntityList();
		} else {
			this.entityList = null;
		}

		this.entityID = entityID;
		this.id = id;
		this.entityFactory = entityFactory;
		this.map = map;
	}

	public boolean isValid()                      { return entityList.containsID(id); }

	public short getEntityID()                    { return entityID; }

	public int getID()                            { return id; }

	public EntityFactorySystem getEntityFactory() { return entityFactory; }

	public CoreMap getMap()                       { return map; }

	protected INetworkAdapter getNetworkConnection() {
		return networkConnection;
	}

	public void addComponent(Component component) {
		entityList.addComponent(this, component);
	}

	public <T extends Component> boolean hasComponent(Class<T> componentClass) {
		return entityList.hasComponent(this, componentClass);
	}

	public <T extends Component> T getComponent(Class<T> componentClass) {
		return entityList.getComponent(this, componentClass);
	}

	public Collection<Component> getComponents() {
		return entityList.getComponents(this);
	}

}
