package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Ray;
import de.nerogar.noise.util.Vector3f;

public interface IReadOnlyCamera extends IMultiCamera {

	public abstract float getAspect();

	public Matrix4f getViewMatrix();

	public Matrix4f getProjectionMatrix();

	public IViewRegion getViewRegion();

	/**
	 * @return The camera yaw in radiants
	 */
	public float getYaw();

	/**
	 * @return The camera pitch in radiants
	 */
	public float getPitch();

	/**
	 * @return The camera roll in radiants
	 */
	public float getRoll();

	public float getX();

	public float getY();

	public float getZ();

	/**
	 * Transform a point in world space to view space.
	 *
	 * @param point the point to transform
	 */
	public void pointToViewSpace(Vector3f point);

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	public void directionToViewSpace(Vector3f direction);

	/**
	 * Transform a point in view space to world space.
	 *
	 * @param point the point to transform
	 */
	public void pointToWorldSpace(Vector3f point);

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	public void directionToWorldSpace(Vector3f direction);

	/**
	 * <p>
	 * Unproject a single pixel to a ray in world space.
	 * The pixel coordinates are in normalized screen coordinates.
	 * (0, 0) is the center, (1, 1) is the top right.
	 * </p>
	 * <p>
	 * The ray is in the following format: ray.pos + ray.dir has a z value of 1 in screen space.
	 * </p>
	 *
	 * @param x the x component of the pixel
	 * @param y the y component of the pixel
	 * @return the ray in world space
	 */
	public Ray unproject(float x, float y);

	/**
	 * Calculate the length a line from (0, 0, -1) to (0, 1, -1) in view space would have in screen space
	 *
	 * @return the unit zize
	 */
	public float getUnitSize();

	public Vector3f getDirectionRight();

	public Vector3f getDirectionUp();

	public Vector3f getDirectionAt();

	/**
	 * returns the equivalent of "unproject(1, 0)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	public Ray getUnitRayRight();

	/**
	 * returns the equivalent of "unproject(0, 1)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	public Ray getUnitRayTop();

	/**
	 * returns the same as unproject(0, 0)
	 *
	 * @return the ray
	 */
	public Ray getUnitRayCenter();

}
