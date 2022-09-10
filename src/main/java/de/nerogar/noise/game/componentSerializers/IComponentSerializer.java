package de.nerogar.noise.game.componentSerializers;

import de.nerogar.noise.game.systems.EntityContainerSystem;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noiseInterface.game.IComponent;

public interface IComponentSerializer<T extends IComponent> {

	void serialize(NDSNodeObject node, T component);

	void deserialize(NDSNodeObject node, T component, EntityContainerSystem entities);

}
