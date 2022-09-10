package de.nerogar.noise.game.componentSerializers;

import de.nerogar.noise.game.systems.EntityContainerSystem;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noiseInterface.game.IComponent;

public class DefaultComponentSerializer implements IComponentSerializer<IComponent> {

	@Override
	public void serialize(NDSNodeObject node, IComponent component) {

	}

	@Override
	public void deserialize(NDSNodeObject node, IComponent component, EntityContainerSystem entities) {

	}

}
