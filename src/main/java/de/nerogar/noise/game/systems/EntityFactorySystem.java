package de.nerogar.noise.game.systems;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.AbstractGameSystem;
import de.nerogar.noiseInterface.game.IComponent;

public class EntityFactorySystem extends AbstractGameSystem {

	public EntityFactorySystem(EventManager eventManager) {
		super(eventManager);
	}

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
