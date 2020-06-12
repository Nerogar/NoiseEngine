package de.nerogar.noiseInterface.game;

public interface IComponent {

	/**
	 * Returns true, if the state of this component has changed since the last call to {@code resetChangedState}
	 * @return true, if the state has changed.
	 */
	default boolean hasChanged()     { return false; }

	/**
	 * Resets the changed state back to false.
	 * @see IComponent#hasChanged()
	 */
	default void resetChangedState() {}

}
