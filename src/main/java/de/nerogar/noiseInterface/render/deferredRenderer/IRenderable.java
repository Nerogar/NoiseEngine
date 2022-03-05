package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.math.Transformation;

import java.util.List;

public interface IRenderable {

	/**
	 * Returns the render properties of this renderable.
	 *
	 * @return the render properties of this renderable.
	 */
	public Transformation getTransformation();

	/**
	 * Sets the render properties of the parent.
	 * If no parent exists, or the rendering should be independent of the parents render properties, null should be passed.
	 *
	 * @param parentTransformation the render properties of the parent.
	 */
	public void setParentTransformation(Transformation parentTransformation);

	/**
	 * Renders the geometry of the scene to a gBuffer.
	 *
	 * @param renderContext the render context containing information about the rendering process
	 */
	public default void renderGeometry(IRenderContext renderContext) {}

	/**
	 * Adds all lights within this renderable to {@code lights}.
	 *
	 * @param lights the list of all lights
	 */
	public default void getLights(List<ILight> lights) {}

}
