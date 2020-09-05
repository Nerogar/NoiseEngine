package de.nerogar.noise.render;

import de.nerogar.noiseInterface.math.IMatrix4f;

public abstract class RenderProperties<T extends RenderProperties<T>> {

	private boolean isVisible = true;

	public interface RenderPropertiesListener<T extends RenderProperties<T>> {

		public void update(T t, boolean position, boolean rotation, boolean scale);
	}

	protected RenderPropertiesListener<T> listener;

	public abstract IMatrix4f getModelMatrix();

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Set the listener for this renderProperties instance.
	 * Only one listener can be attached.
	 * Call {@code setListener(null)} to clear the listener
	 *
	 * @param listener the new listener
	 */
	public void setListener(RenderPropertiesListener<T> listener) {
		this.listener = listener;
	}

}
