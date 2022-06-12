package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.ViewFrustum;
import de.nerogar.noise.math.Matrix4fUtils;

import java.util.Locale;

public class PerspectiveCamera extends DefaultCamera {

	private float fov;
	private float aspect;
	private float near;
	private float far;

	public PerspectiveCamera(float fov, float aspect, float near, float far) {
		super(new ViewFrustum());

		setFOV(fov);
		setAspect(aspect);
		setNear(near);
		setFar(far);
	}

	@Override
	protected void setUnitRays() {

		float fovTopRad = getFOV() * PI / 360.0f;
		float h = (float) Math.tan(fovTopRad);
		float w = h * aspect;

		// set rays in view space
		unitRayTop.getStart().set(0, 0, 0);
		unitRayRight.getDir().set(w, 0, -1);

		unitRayRight.getStart().set(0, 0, 0);
		unitRayTop.getDir().set(0, h, -1);

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
		Matrix4fUtils.setPerspectiveProjection(projectionMatrix, fov, aspect, near, far);

		projectionMatrixDirty = false;

		setUnitRays();
		viewRegion.setPlanes(this);
	}

	public void setFOV(float fov) {
		if (this.fov == fov) return;
		this.fov = fov;

		// set unit size:
		float fovRadiants = (fov * PI / 180.0f);
		unitSize = (float) Math.tan(fovRadiants / 2.0f) / 2.0f;

		projectionMatrixDirty = true;
	}

	public float getFOV() {
		return fov;
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
		return String.format(Locale.US, "PerspectiveCamera(fov: %.2f, aspect: %.2f, yaw:%.2f, pitch:%.2f, roll:%.2f, x:%.2f, y:%.2f, z:%.2f)", fov, aspect,
		                     transformation.getYaw(), transformation.getPitch(), transformation.getRoll(), transformation.getX(), transformation.getY(), transformation.getZ());
	}

}
