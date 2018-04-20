package de.nerogar.noise.game.core.systems;

import de.nerogar.noise.game.*;
import de.nerogar.noise.game.core.components.PositionComponent;
import de.nerogar.noise.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityFactorySystem extends LogicSystem {

	private final boolean useNegativeIDs;
	private int currentMaxID = 1;

	private GameObjectsSystem gameObjectsSystem;

	protected CoreMap                     map;
	protected Map<Short, List<Component>> entityBlueprints;

	public EntityFactorySystem(CoreMap map, boolean useNegativeIDs) {
		this.map = map;
		this.useNegativeIDs = useNegativeIDs;

		entityBlueprints = new HashMap<>();
	}

	@Override
	public void init() {
		gameObjectsSystem = map.getGameSystem(GameObjectsSystem.class);
	}

	protected int generateID() {
		if (useNegativeIDs) {
			return -(++currentMaxID);
		} else {
			return ++currentMaxID;
		}
	}

	protected void takeID(int id) {
		if (useNegativeIDs) {
			if (id < 0 && (-id) > currentMaxID) currentMaxID = -id;
		} else {
			if (id > 0 && id > currentMaxID) currentMaxID = id;
		}
	}

	private void createSingleEntity(short entityID, List<Component> components) {
		List<Component> blueprint = gameObjectsSystem.getBlueprint(entityID);

		for (Component blueprintComponent : blueprint) {
			components.add(blueprintComponent.clone());
		}

	}

	public final Entity createEntity(short entityID, float x, float y, float z) {
		return createEntity(entityID, generateID(), x, y, z);
	}

	public final Entity createEntity(short entityID, int id, float x, float y, float z) {
		if (map.getEntityList().containsID(id)) {
			NoiseGame.logger.log(Logger.WARNING, "duplicate entity spawn, id: " + id);
			return map.getEntityList().get(id);
		}
		Entity entity = new Entity(entityID, id, this, map);
		List<Component> components = new ArrayList<>();
		components.add(new PositionComponent(x, y, z));
		createSingleEntity(entityID, components);
		map.getEntityList().put(entity, components);

		takeID(id);

		return entity;
	}

}
