package de.nerogar.noiseInterface.game;

public interface IComponent {

	/**
	 * Sets the entity id of this component.
	 *
	 * @param entity the entity
	 */
	void setEntity(long entity);

	/**
	 * Gets the entity of this component.
	 *
	 * @return the entity for this component
	 */
	long getEntity();

}
