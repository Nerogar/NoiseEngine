package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4fUtils;

import java.util.Locale;

public class OrthographicCamera extends Camera {

	private float height;
	private float aspect;
	private float near;
	private float far;

	public OrthographicCamera(float height, float aspect, float near, float far) {
		super(new ViewBox());

		setHeight(height);
		setAspect(aspect);
		setNear(near);
		setFar(far);
	}

	@Override
	protected void setUnitRays() {

		// set rays in view space
		unitRayTop.getStart().set(0, height/2, 0);
		unitRayRight.getDir().set(0, 0, -1);

		unitRayRight.getStart().set(height * aspect/2, 0, 0);
		unitRayTop.getDir().set(0, 0, -1);

		unitRayCenter.getStart().set(0, 0, 0);
		unitRayCenter.getDir().set(0, 0, -1);

		// transform rays to world space
		directionToWorldSpace(unitRayRight.getDir());
		directionToWorldSpace(unitRayTop.getDir());
		directionToWorldSpace(unitRayCenter.getDir());

		pointToWorldSpace(unitRayRight.getStart());
		pointToWorldSpace(unitRayTop.getStart());
		pointToWorldSpace(unitRayCenter.getStart());

		// calculate difference
		unitRayRight.getStart().subtract(unitRayCenter.getStart());
		unitRayTop.getStart().subtract(unitRayCenter.getStart());

		unitRayRight.getDir().subtract(unitRayCenter.getDir());
		unitRayTop.getDir().subtract(unitRayCenter.getDir());

	}

	@Override
	protected void setProjectionMatrix() {
		float width = height * aspect;
		Matrix4fUtils.setOrthographicProjection(projectionMatrix, -width / 2f, width / 2f, height / 2f, -height / 2f, near, far);

		projectionMatrixDirty = false;

		setUnitRays();
		viewRegion.setPlanes(this);
	}

	public void setHeight(float height) {
		if (this.height == height) return;
		this.height = height;

		// set unit size:
		unitSize = 1.0f / height;

		projectionMatrixDirty = true;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public void setAspect(float aspect) {
		if (this.aspect == aspect) return;
		this.aspect = aspect;
		projectionMatrixDirty = true;
	}

	@Override
	public float getAspect() {
		return aspect;
	}

	public void setNear(float near) {
		this.near = near;
		projectionMatrixDirty = true;
	}

	public float getNear() {
		return near;
	}

	public void setFar(float far) {
		this.far = far;
		projectionMatrixDirty = true;
	}

	public float getFar() {
		return far;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "OrthographicCamera(height: %.2f, aspect: %.2f, yaw:%.2f, pitch:%.2f, roll:%.2f, x:%.2f, y:%.2f, z:%.2f)", height, aspect, yaw, pitch, roll, x, y, z);
	}

}
