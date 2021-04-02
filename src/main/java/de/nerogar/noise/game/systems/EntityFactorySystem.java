package de.nerogar.noise.game.systems;

import de.nerogar.noiseInterface.game.IComponent;
import de.nerogar.noiseInterface.game.IGameSystem;

public class EntityFactorySystem implements IGameSystem {

	public void setEntityDefinitions() {
	}

	public IComponent[] createEntity(int entityTypeId) {
		return null;
	}

	public IComponent[] getBlueprint(int entityTypeId) {
		return null;
	}

	public <T extends IComponent> T getBlueprintComponent(int entityTypeId, Class<T> componentClass) {
		return null;
	}

}
