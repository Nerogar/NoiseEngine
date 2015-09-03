package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noise.util.Matrix4f;

public interface Effect {

	/**
	 * The renderProperties of this effect.
	 * 
	 * @return the renderProperties
	 */
	public RenderProperties3f getRenderProperties();

	/**
	 * The maximum radius of this effect. Used for frustum culling.
	 * 
	 * @return the bounding radius
	 */
	public float getBoundingRadius();

	public void render(Matrix4f viewMatrix, Matrix4f projectionMatrix);

}
