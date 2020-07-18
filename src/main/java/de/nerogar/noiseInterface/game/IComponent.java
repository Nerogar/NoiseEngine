package de.nerogar.noiseInterface.game;

public interface IComponent {

	/**
	 * Sets the entity of this component.
	 *
	 * @param entity the entity
	 */
	void setEntity(IEntity entity);

	/**
	 * Gets the entity of this component.
	 *
	 * @return the entity for this component
	 */
	IEntity getEntity();

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
