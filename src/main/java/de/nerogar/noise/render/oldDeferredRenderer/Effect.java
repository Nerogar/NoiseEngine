package de.nerogar.noise.render.oldDeferredRenderer;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noise.util.Matrix4f;

/**
 * an interface for effects used in the {@link DeferredRenderer DeferredRenderer}
 */
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

	/**
	 * renders this effect
	 * 
	 * @param viewMatrix the view matrix of the camera
	 * @param projectionMatrix the projection matrix of the camera
	 */
	public void render(Matrix4f viewMatrix, Matrix4f projectionMatrix);

}
