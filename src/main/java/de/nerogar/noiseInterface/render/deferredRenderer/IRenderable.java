package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noiseInterface.math.ITransformation;

import java.util.List;

public interface IRenderable {

	/**
	 * Returns the transformation of this renderable.
	 *
	 * @return the transformation of this renderable.
	 */
	ITransformation getTransformation();

	/**
	 * Sets the transformation of this renderable.
	 *
	 * @param transformation the new transformation.
	 */
	void setTransformation(ITransformation transformation);

	/**
	 * Renders the geometry of the scene to a gBuffer.
	 *
	 * @param renderContext the render context containing information about the rendering process
	 */
	default void renderGeometry(IRenderContext renderContext) {}

	/**
	 * Adds all lights within this renderable to {@code lights}.
	 *
	 * @param lights the list of all lights
	 */
	default void getLights(List<ILight> lights) {}

}
