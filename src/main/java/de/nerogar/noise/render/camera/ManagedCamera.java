package de.nerogar.noise.render.camera;

import de.nerogar.noise.math.*;
import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.render.ViewRegionAll;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.*;

public class ManagedCamera implements IReadOnlyCamera {

	private IMatrix4f viewMatrix;
	private IMatrix4f projectionMatrix;

	private IMatrix4f inverseViewMatrix;
	private IMatrix4f inverseProjectionMatrix;

	private final IReadOnlyCamera[] cameraAsArray;
	private       ViewRegionAll     viewRegionAll;

	public ManagedCamera() {
		this.viewMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();

		cameraAsArray = new IReadOnlyCamera[] { this };
		viewRegionAll = new ViewRegionAll();

		inverseViewMatrix = new Matrix4f();
		inverseProjectionMatrix = new Matrix4f();
	}

	public void manage(IMatrix4f viewMatrix, IMatrix4f projectionMatrix) {
		this.viewMatrix.set(viewMatrix);
		this.projectionMatrix.set(projectionMatrix);

		inverseViewMatrix.set(viewMatrix);
		inverseViewMatrix.invert();

		inverseProjectionMatrix.set(projectionMatrix);
		inverseProjectionMatrix.invert();
	}

	@Override
	public float getAspect() {
		return 0;
	}

	@Override
	public IMatrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public IMatrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public IViewRegion getViewRegion() {
		return viewRegionAll;
	}

	@Override
	public float getYaw() {
		return 0;
	}

	@Override
	public float getPitch() {
		return 0;
	}

	@Override
	public float getRoll() {
		return 0;
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}

	@Override
	public float getZ() {
		return 0;
	}

	@Override
	public void pointToViewSpace(IVector3f point) {

	}

	@Override
	public void directionToViewSpace(IVector3f direction) {

	}

	@Override
	public void pointToWorldSpace(IVector3f point) {

	}

	@Override
	public void directionToWorldSpace(IVector3f direction) {

	}

	@Override
	public IVector2f project(IReadonlyVector3f point) {
		IVector3f pointInScreenSpace = point.transformed(viewMatrix, 1).transform(projectionMatrix, 1);

		return new Vector2f(
				pointInScreenSpace.getX() / pointInScreenSpace.getZ(),
				pointInScreenSpace.getY() / pointInScreenSpace.getZ()
		);
	}

	@Override
	public Ray unproject(float x, float y) {
		Vector3f pos = new Vector3f().transform(inverseViewMatrix, 1);
		Vector3f dir = new Vector3f(x, y, 1).transform(inverseProjectionMatrix, 1);

		dir.multiply(-1.0f / dir.getZ());
		dir.transform(inverseViewMatrix);

		return new Ray(pos, dir);
	}

	@Override
	public float getUnitSize() {
		return 0;
	}

	@Override
	public Vector3f getDirectionRight() {
		return null;
	}

	@Override
	public Vector3f getDirectionUp() {
		return null;
	}

	@Override
	public IVector3f getDirectionAt() {
		return null;
	}

	@Override
	public Ray getUnitRayRight() {
		return null;
	}

	@Override
	public Ray getUnitRayTop() {
		return null;
	}

	@Override
	public Ray getUnitRayCenter() {
		return null;
	}

	@Override
	public IReadOnlyCamera[] cameras() {
		return cameraAsArray;
	}
}
