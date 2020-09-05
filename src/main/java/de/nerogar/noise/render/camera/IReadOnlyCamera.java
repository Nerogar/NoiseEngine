package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IVector3f;

public interface IReadOnlyCamera extends IMultiCamera {

	float getAspect();

	IMatrix4f getViewMatrix();

	IMatrix4f getProjectionMatrix();

	IViewRegion getViewRegion();

	/**
	 * @return The camera yaw in radiants
	 */
	float getYaw();

	/**
	 * @return The camera pitch in radiants
	 */
	float getPitch();

	/**
	 * @return The camera roll in radiants
	 */
	float getRoll();

	float getX();

	float getY();

	float getZ();

	/**
	 * Transform a point in world space to view space.
	 *
	 * @param point the point to transform
	 */
	void pointToViewSpace(IVector3f point);

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	void directionToViewSpace(IVector3f direction);

	/**
	 * Transform a point in view space to world space.
	 *
	 * @param point the point to transform
	 */
	void pointToWorldSpace(IVector3f point);

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	void directionToWorldSpace(IVector3f direction);

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
	Ray unproject(float x, float y);

	/**
	 * Calculate the length a line from (0, 0, -1) to (0, 1, -1) in view space would have in screen space
	 *
	 * @return the unit zize
	 */
	float getUnitSize();

	IVector3f getDirectionRight();

	IVector3f getDirectionUp();

	IVector3f getDirectionAt();

	/**
	 * returns the equivalent of "unproject(1, 0)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	Ray getUnitRayRight();

	/**
	 * returns the equivalent of "unproject(0, 1)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	Ray getUnitRayTop();

	/**
	 * returns the same as unproject(0, 0)
	 *
	 * @return the ray
	 */
	Ray getUnitRayCenter();

}
