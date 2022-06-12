package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.*;

public interface IReadOnlyCamera extends IMultiCamera {

	float getAspect();

	IMatrix4f getViewMatrix();

	IMatrix4f getProjectionMatrix();

	IViewRegion getViewRegion();

	IReadOnlyTransformation getTransformation();

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
	 * Projects a point in world space onto the screen.
	 * The resulting coordinates are in normalized screen coordinates.
	 * (0, 0) is the center, (1, 1) is the top right.
	 *
	 * @param point the point in world space
	 * @return the projected point in screen space
	 */
	IVector2f project(IReadonlyVector3f point);

	/**
	 * Unproject a single pixel to a ray in world space.
	 * The pixel coordinates are in normalized screen coordinates.
	 * (0, 0) is the center, (1, 1) is the top right.
	 * The ray is in the following format: ray.pos + ray.dir has a z value of 1 in screen space.
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
