package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.render.ViewRegionAll;
import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Ray;
import de.nerogar.noise.util.Vector3f;

public class ManagedCamera implements IReadOnlyCamera {

	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;

	private Matrix4f inverseViewMatrix;
	private Matrix4f inverseProjectionMatrix;

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

	public void manage(Matrix4f viewMatrix, Matrix4f projectionMatrix) {
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
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Matrix4f getProjectionMatrix() {
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
	public void pointToViewSpace(Vector3f point) {

	}

	@Override
	public void directionToViewSpace(Vector3f direction) {

	}

	@Override
	public void pointToWorldSpace(Vector3f point) {

	}

	@Override
	public void directionToWorldSpace(Vector3f direction) {

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
	public Vector3f getDirectionAt() {
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
