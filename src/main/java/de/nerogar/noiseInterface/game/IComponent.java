package de.nerogar.noiseInterface.game;

import de.nerogar.noise.game.EntityContainer;

public interface IComponent {

	/**
	 * Sets the entity id of this component.
	 *
	 * @param entityId the entity id
	 */
	void setEntityId(int entityId);

	/**
	 * Gets the entity id of this component.
	 *
	 * @return the entity id for this component
	 */
	int getEntityId();

	/**
	 * Returns true, if the state of this component has changed since the last call to {@code resetChangedState}
	 *
	 * @return true, if the state has changed.
	 */
	default boolean hasChanged() { return false; }

	/**
	 * Resets the changed state back to false.
	 *
	 * @see IComponent#hasChanged()
	 */
	default void resetChangedState() {}

}
