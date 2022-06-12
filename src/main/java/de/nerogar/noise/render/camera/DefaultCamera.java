package de.nerogar.noise.render.camera;

import de.nerogar.noise.math.*;
import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.*;

public abstract class DefaultCamera extends Camera {

	protected static final float PI = (float) Math.PI;

	protected ITransformation transformation;
	protected int             directionsModCount;

	private   Matrix4f viewMatrix;
	protected int      viewMatrixModCount;

	protected boolean   projectionMatrixDirty = true;
	protected IMatrix4f projectionMatrix;

	protected IVector3f directionRight;
	protected IVector3f directionUp;
	protected IVector3f directionAt;

	protected Ray   unitRayRight;
	protected Ray   unitRayTop;
	protected Ray   unitRayCenter;
	protected float unitSize;

	protected IViewRegion viewRegion;

	private final IReadOnlyCamera[] cameraAsArray;

	public DefaultCamera(IViewRegion viewRegion) {
		cameraAsArray = new IReadOnlyCamera[] { this };

		transformation = new Transformation();
		directionsModCount = transformation.getModCount();
		viewMatrix = new Matrix4f();
		viewMatrixModCount = transformation.getModCount();

		directionRight = new Vector3f();
		directionUp = new Vector3f();
		directionAt = new Vector3f();

		unitRayRight = new Ray(new Vector3f(), new Vector3f());
		unitRayTop = new Ray(new Vector3f(), new Vector3f());
		unitRayCenter = new Ray(new Vector3f(), new Vector3f());

		projectionMatrix = new Matrix4f();

		this.viewRegion = viewRegion;
	}

	@Override
	public IReadOnlyCamera[] cameras() {return cameraAsArray;}

	@Override
	public void setTransformation(ITransformation transformation) {this.transformation = transformation;}

	@Override
	public ITransformation getTransformation() {return transformation;}

	protected abstract void setUnitRays();

	private void setDirections() {
		if (directionsModCount != transformation.getModCount()) {
			// world space directions
			directionRight.set(1.0f, 0.0f, 0.0f);
			directionToWorldSpace(directionRight);

			directionUp.set(0.0f, 1.0f, 0.0f);
			directionToWorldSpace(directionUp);

			directionAt.set(0.0f, 0.0f, -1.0f);
			directionToWorldSpace(directionAt);

			// unit distance rays
			setUnitRays();

			viewRegion.setPlanes(this);

			directionsModCount = transformation.getModCount();
		}
	}

	public IMatrix4f getViewMatrix() {
		if (viewMatrixModCount != transformation.getModCount() || transformation.hasParentChanged()) {
			viewMatrix.set(transformation.getModelMatrix()).invert();
			viewMatrixModCount = transformation.getModCount();
		}

		return viewMatrix;
	}

	protected abstract void setProjectionMatrix();

	public IMatrix4f getProjectionMatrix() {
		if (projectionMatrixDirty) setProjectionMatrix();

		return projectionMatrix;
	}

	public IViewRegion getViewRegion() {
		if (projectionMatrixDirty) setProjectionMatrix();
		setDirections();

		return viewRegion;
	}

	/**
	 * Transform a point in world space to view space.
	 *
	 * @param point the point to transform
	 */
	public void pointToViewSpace(IVector3f point) {
		float newX, newY, newZ;

		IMatrix4f viewMatrix = getViewMatrix();

		newX = point.getX() * viewMatrix.get(0, 0);
		newY = point.getX() * viewMatrix.get(1, 0);
		newZ = point.getX() * viewMatrix.get(2, 0);

		newX += point.getY() * viewMatrix.get(0, 1);
		newY += point.getY() * viewMatrix.get(1, 1);
		newZ += point.getY() * viewMatrix.get(2, 1);

		newX += point.getZ() * viewMatrix.get(0, 2);
		newY += point.getZ() * viewMatrix.get(1, 2);
		newZ += point.getZ() * viewMatrix.get(2, 2);

		newX += viewMatrix.get(0, 3);
		newY += viewMatrix.get(1, 3);
		newZ += viewMatrix.get(2, 3);

		point.setX(newX);
		point.setY(newY);
		point.setZ(newZ);
	}

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	public void directionToViewSpace(IVector3f direction) {
		float newX, newY, newZ;

		IMatrix4f viewMatrix = getViewMatrix();

		newX = direction.getX() * viewMatrix.get(0, 0);
		newY = direction.getX() * viewMatrix.get(1, 0);
		newZ = direction.getX() * viewMatrix.get(2, 0);

		newX += direction.getY() * viewMatrix.get(0, 1);
		newY += direction.getY() * viewMatrix.get(1, 1);
		newZ += direction.getY() * viewMatrix.get(2, 1);

		newX += direction.getZ() * viewMatrix.get(0, 2);
		newY += direction.getZ() * viewMatrix.get(1, 2);
		newZ += direction.getZ() * viewMatrix.get(2, 2);

		direction.setX(newX);
		direction.setY(newY);
		direction.setZ(newZ);
	}

	/**
	 * Transform a point in view space to world space.
	 *
	 * @param point the point to transform
	 */
	public void pointToWorldSpace(IVector3f point) {
		float newX, newY, newZ;

		IMatrix4f viewMatrix = getViewMatrix();

		point.addX(-viewMatrix.get(0, 3));
		point.addY(-viewMatrix.get(1, 3));
		point.addZ(-viewMatrix.get(2, 3));

		newX = point.getX() * viewMatrix.get(0, 0);
		newY = point.getX() * viewMatrix.get(0, 1);
		newZ = point.getX() * viewMatrix.get(0, 2);

		newX += point.getY() * viewMatrix.get(1, 0);
		newY += point.getY() * viewMatrix.get(1, 1);
		newZ += point.getY() * viewMatrix.get(1, 2);

		newX += point.getZ() * viewMatrix.get(2, 0);
		newY += point.getZ() * viewMatrix.get(2, 1);
		newZ += point.getZ() * viewMatrix.get(2, 2);

		point.setX(newX);
		point.setY(newY);
		point.setZ(newZ);
	}

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	public void directionToWorldSpace(IVector3f direction) {
		float newX, newY, newZ;

		IMatrix4f viewMatrix = getViewMatrix();

		newX = direction.getX() * viewMatrix.get(0, 0);
		newY = direction.getX() * viewMatrix.get(0, 1);
		newZ = direction.getX() * viewMatrix.get(0, 2);

		newX += direction.getY() * viewMatrix.get(1, 0);
		newY += direction.getY() * viewMatrix.get(1, 1);
		newZ += direction.getY() * viewMatrix.get(1, 2);

		newX += direction.getZ() * viewMatrix.get(2, 0);
		newY += direction.getZ() * viewMatrix.get(2, 1);
		newZ += direction.getZ() * viewMatrix.get(2, 2);

		direction.setX(newX);
		direction.setY(newY);
		direction.setZ(newZ);
	}

	@Override
	public IVector2f project(IReadonlyVector3f point) {
		IMatrix4f viewMatrix = getViewMatrix();

		IVector3f pointInScreenSpace = point.transformed(viewMatrix, 1).transform(projectionMatrix, 1);

		return new Vector2f(
				pointInScreenSpace.getX() / pointInScreenSpace.getZ(),
				pointInScreenSpace.getY() / pointInScreenSpace.getZ()
		);
	}

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
	public Ray unproject(float x, float y) {
		IVector3f start = new Vector3f(
				getUnitRayCenter().getStart().getX() + getUnitRayRight().getStart().getX() * x + getUnitRayTop().getStart().getX() * y,
				getUnitRayCenter().getStart().getY() + getUnitRayRight().getStart().getY() * x + getUnitRayTop().getStart().getY() * y,
				getUnitRayCenter().getStart().getZ() + getUnitRayRight().getStart().getZ() * x + getUnitRayTop().getStart().getZ() * y
		);

		IVector3f dir = new Vector3f(
				getUnitRayCenter().getDir().getX() + getUnitRayRight().getDir().getX() * x + getUnitRayTop().getDir().getX() * y,
				getUnitRayCenter().getDir().getY() + getUnitRayRight().getDir().getY() * x + getUnitRayTop().getDir().getY() * y,
				getUnitRayCenter().getDir().getZ() + getUnitRayRight().getDir().getZ() * x + getUnitRayTop().getDir().getZ() * y
		);

		return new Ray(start, dir);
	}

	/**
	 * Calculate the length a line from (0, 0, -1) to (0, 1, -1) in view space would have in screen space
	 *
	 * @return the unit zize
	 */
	public float getUnitSize() {
		return unitSize;
	}

	public IVector3f getDirectionRight() {
		setDirections();

		return directionRight;
	}

	public IVector3f getDirectionUp() {
		setDirections();

		return directionUp;
	}

	public IVector3f getDirectionAt() {
		setDirections();

		return directionAt;
	}

	/**
	 * returns the equivalent of "unproject(1, 0)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	public Ray getUnitRayRight() {
		setDirections();

		return unitRayRight;
	}

	/**
	 * returns the equivalent of "unproject(0, 1)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	public Ray getUnitRayTop() {
		setDirections();

		return unitRayTop;
	}

	/**
	 * returns the same as unproject(0, 0)
	 *
	 * @return the ray
	 */
	public Ray getUnitRayCenter() {
		setDirections();

		return unitRayCenter;
	}

}
