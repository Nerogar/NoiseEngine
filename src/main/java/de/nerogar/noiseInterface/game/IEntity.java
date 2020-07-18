package de.nerogar.noiseInterface.game;

public interface IEntity {

	int getId();

	<T extends IComponent> T getComponent(Class<T> componentClass);

	IComponent[] getComponents();

}
