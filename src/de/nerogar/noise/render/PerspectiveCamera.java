package de.nerogar.noise.render;

import java.util.Locale;

import de.nerogar.noise.util.*;

public class PerspectiveCamera {

	private float yaw, pitch, roll;
	private float x, y, z;

	private Matrix4f positionMatrix;
	private Matrix4f yawMatrix;
	private Matrix4f pitchMatrix;
	private Matrix4f rollMatrix;

	private boolean viewMatrixDirty = true;
	private Matrix4f viewMatrix;

	private Vector3f directionRight;
	private Vector3f directionUp;
	private Vector3f directionAt;

	private float fov;
	private float aspect;
	private float near;
	private float far;

	private boolean projectionMatrixDirty = true;
	private Matrix4f projectionMatrix;

	private ViewFrustum frustum;

	public PerspectiveCamera(float fov, float aspect, float near, float far) {
		positionMatrix = new Matrix4f();
		yawMatrix = new Matrix4f();
		pitchMatrix = new Matrix4f();
		rollMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();

		directionRight = new Vector3f();
		directionUp = new Vector3f();
		directionAt = new Vector3f();

		projectionMatrix = new Matrix4f();

		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;

		frustum = new ViewFrustum();

		setPositionMatrix();
		setYawMatrix();
		setPitchMatrix();
		setRollMatrix();
	}

	private void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, -x, -y, -z);
		viewMatrixDirty = true;
	}

	private void setYawMatrix() {
		Matrix4fUtils.setYawMatrix(yawMatrix, -yaw);
		viewMatrixDirty = true;
	}

	private void setPitchMatrix() {
		Matrix4fUtils.setPitchMatrix(pitchMatrix, pitch);
		viewMatrixDirty = true;
	}

	private void setRollMatrix() {
		Matrix4fUtils.setRollMatrix(rollMatrix, -roll);
		viewMatrixDirty = true;
	}

	private void setViewMatrix() {
		viewMatrixDirty = false;

		viewMatrix.set(positionMatrix);
		viewMatrix.multiplyLeft(yawMatrix);
		viewMatrix.multiplyLeft(pitchMatrix);
		viewMatrix.multiplyLeft(rollMatrix);

		setDirections();

		frustum.setPlanes(this);
	}

	private void setDirections() {
		directionRight.set(1.0f, 0.0f, 0.0f);
		directionToWorldSpace(directionRight);

		directionUp.set(0.0f, 1.0f, 0.0f);
		directionToWorldSpace(directionUp);

		directionAt.set(0.0f, 0.0f, -1.0f);
		directionToWorldSpace(directionAt);
	}

	public Matrix4f getViewMatrix() {
		if (viewMatrixDirty) setViewMatrix();

		return viewMatrix;
	}

	private void setProjectionMatrix() {
		Matrix4fUtils.setPerspectiveProjection(projectionMatrix, fov, aspect, near, far);

		projectionMatrixDirty = false;

		frustum.setPlanes(this);
	}

	public Matrix4f getProjectionMatrix() {
		if (projectionMatrixDirty) setProjectionMatrix();

		return projectionMatrix;
	}

	public ViewFrustum getViewFrustum() {
		if (viewMatrixDirty) setViewMatrix();
		if (projectionMatrixDirty) setProjectionMatrix();

		return frustum;
	}

	/**
	 * Sets the camera yaw in radiants 
	 */
	public void setYaw(float yaw) {
		if (this.yaw == yaw) return;
		this.yaw = yaw;
		setYawMatrix();
	}

	/**
	 * @return The camera yaw in radiants 
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Sets the camera pitch in radiants 
	 */
	public void setPitch(float pitch) {
		if (this.pitch == pitch) return;
		this.pitch = pitch;
		setPitchMatrix();
	}

	/**
	 * @return The camera pitch in radiants 
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Sets the camera roll in radiants 
	 */
	public void setRoll(float roll) {
		if (this.roll == roll) return;
		this.roll = roll;
		setRollMatrix();
	}

	/**
	 * @return The camera roll in radiants 
	 */
	public float getRoll() {
		return roll;
	}

	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;
		setPositionMatrix();
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;
		setPositionMatrix();
	}

	public float getY() {
		return y;
	}

	public void setZ(float z) {
		if (this.z == z) return;
		this.z = z;
		setPositionMatrix();
	}

	public float getZ() {
		return z;
	}

	public void setXYZ(float x, float y, float z) {
		if (this.x == x && this.y == y && this.z == z) return;
		this.x = x;
		this.y = y;
		this.z = z;
		setPositionMatrix();
	}

	public void setFOV(float fov) {
		if (this.fov == fov) return;
		this.fov = fov;
		projectionMatrixDirty = true;
	}

	public float getFOV() {
		return fov;
	}

	public void setAspect(float aspect) {
		if (this.aspect == aspect) return;
		this.aspect = aspect;
		projectionMatrixDirty = true;
	}

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

	/**
	 * Transform a point in world space to view space.
	 * 
	 * @param point the point to transform
	 */
	protected void pointToViewSpace(Vector3f point) {
		float newX, newY, newZ;

		newX = point.getX() * getViewMatrix().get(0, 0);
		newY = point.getX() * viewMatrix.get(0, 1);
		newZ = point.getX() * viewMatrix.get(0, 2);

		newX += point.getY() * viewMatrix.get(1, 0);
		newY += point.getY() * viewMatrix.get(1, 1);
		newZ += point.getY() * viewMatrix.get(1, 2);

		newX += point.getZ() * viewMatrix.get(2, 0);
		newY += point.getZ() * viewMatrix.get(2, 1);
		newZ += point.getZ() * viewMatrix.get(2, 2);

		newX += viewMatrix.get(3, 0);
		newY += viewMatrix.get(3, 1);
		newZ += viewMatrix.get(3, 2);

		point.setX(newX);
		point.setY(newY);
		point.setZ(newZ);
	}

	/**
	 * Transform a direction in world space to view space.
	 * 
	 * @param direction the direction to transform
	 */
	protected void directionToViewSpace(Vector3f direction) {
		float newX, newY, newZ;

		newX = direction.getX() * getViewMatrix().get(0, 0);
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

	/**
	 * Transform a direction in world space to view space.
	 * 
	 * @param direction the direction to transform
	 */
	protected void directionToWorldSpace(Vector3f direction) {
		float newX, newY, newZ;

		newX = direction.getX() * getViewMatrix().get(0, 0);
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

	public Vector3f getDirectionRight() {
		if (viewMatrixDirty) setViewMatrix();

		return directionRight;
	}

	public Vector3f getDirectionUp() {
		if (viewMatrixDirty) setViewMatrix();

		return directionUp;
	}

	public Vector3f getDirectionAt() {
		if (viewMatrixDirty) setViewMatrix();

		return directionAt;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "Camera(yaw:%.2f, pitch:%.2f, roll:%.2f, x:%.2f, y:%.2f, z:%.2f)", yaw, pitch, roll, x, y, z);
	}

}
