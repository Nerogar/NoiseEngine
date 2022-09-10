package de.nerogar.noise.game.entityFactories;

import de.nerogar.noiseInterface.game.IComponent;

public interface IEntityFactory {

	IComponent[] createComponents();

	@SuppressWarnings("unchecked")
	default <T extends IComponent> T getComponent(IComponent[] components, Class<T> componentClass) {
		for (IComponent component : components) {
			if (component.getClass().equals(componentClass)) return (T) component;
		}
		return null;
	}

}
