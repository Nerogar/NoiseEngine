package de.nerogar.noise.render;

import de.nerogar.noise.util.*;

public class RenderProperties3f implements RenderProperties {

	private float yaw, pitch, roll;
	private float x, y, z;
	private float scaleX, scaleY, scaleZ, maxScaleComponent;
	private boolean isVisible;

	private boolean positionMatrixDirty = true;
	private Matrix4f positionMatrix;
	private boolean scaleMatrixDirty = true;
	private Matrix4f scaleMatrix;
	private boolean yawMatrixDirty = true;
	private Matrix4f yawMatrix;
	private boolean pitchMatrixDirty = true;
	private Matrix4f pitchMatrix;
	private boolean rollMatrixDirty = true;
	private Matrix4f rollMatrix;

	private boolean modelMatrixDirty = true;
	private Matrix4f modelMatrix;
	private Matrix4f normalMatrix;

	private Matrix4f tempMatrix;

	public RenderProperties3f() {
		this(0, 0, 0, 0, 0, 0);
	}

	public RenderProperties3f(float yaw, float pitch, float roll, float x, float y, float z) {
		positionMatrix = new Matrix4f();
		scaleMatrix = new Matrix4f();
		yawMatrix = new Matrix4f();
		pitchMatrix = new Matrix4f();
		rollMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();

		tempMatrix = new Matrix4f();

		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.x = x;
		this.y = y;
		this.z = z;

		scaleX = 1.0f;
		scaleY = 1.0f;
		scaleZ = 1.0f;
		maxScaleComponent = 1.0f;

		isVisible = true;

		setPositionMatrix();
		setScaleMatrix();
		setYawMatrix();
		setPitchMatrix();
		setRollMatrix();
	}

	@Override
	public Matrix4f getModelMatrix() {
		if (modelMatrixDirty) setModelMatrix();
		return modelMatrix;
	}

	public Matrix4f getNormalMatrix() {
		if (modelMatrixDirty) setModelMatrix();
		return normalMatrix;
	}

	private void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, x, y, z);
		positionMatrixDirty = false;
	}

	private void setScaleMatrix() {
		Matrix4fUtils.setScaleMatrix(scaleMatrix, scaleX, scaleY, scaleZ);
		scaleMatrixDirty = false;
	}

	private void setYawMatrix() {
		Matrix4fUtils.setYawMatrix(yawMatrix, yaw);
		yawMatrixDirty = false;
	}

	private void setPitchMatrix() {
		Matrix4fUtils.setPitchMatrix(pitchMatrix, pitch);
		pitchMatrixDirty = false;
	}

	private void setRollMatrix() {
		Matrix4fUtils.setRollMatrix(rollMatrix, roll);
		rollMatrixDirty = false;
	}

	private void setModelMatrix() {
		if (positionMatrixDirty) setPositionMatrix();
		if (scaleMatrixDirty) setScaleMatrix();
		if (yawMatrixDirty) setYawMatrix();
		if (pitchMatrixDirty) setPitchMatrix();
		if (rollMatrixDirty) setRollMatrix();

		tempMatrix.set(rollMatrix);
		tempMatrix.multiplyLeft(pitchMatrix);
		tempMatrix.multiplyLeft(yawMatrix);
		tempMatrix.multiplyLeft(positionMatrix);

		//model matrix
		modelMatrix.set(scaleMatrix).multiplyLeft(tempMatrix);

		//normal matrix (rotations * scale^-1)
		float sX = scaleMatrix.get(0, 0);
		float sY = scaleMatrix.get(1, 1);
		float sZ = scaleMatrix.get(2, 2);

		scaleMatrix.set(0, 0, 1f / sX);
		scaleMatrix.set(1, 1, 1f / sY);
		scaleMatrix.set(2, 2, 1f / sZ);

		normalMatrix.set(tempMatrix).multiplyRight(scaleMatrix);

		//reset scale matrix
		scaleMatrix.set(0, 0, sX);
		scaleMatrix.set(1, 1, sY);
		scaleMatrix.set(2, 2, sZ);

		modelMatrixDirty = false;
	}

	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Sets the yaw in radiants 
	 */
	public void setYaw(float yaw) {
		if (this.yaw == yaw) return;
		this.yaw = yaw;

		yawMatrixDirty = true;
		modelMatrixDirty = true;
	}

	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Sets the pitch in radiants 
	 */
	public void setPitch(float pitch) {
		if (this.pitch == pitch) return;
		this.pitch = pitch;

		pitchMatrixDirty = true;
		modelMatrixDirty = true;
	}

	/**
	 * @return the roll
	 */
	public float getRoll() {
		return roll;
	}

	/**
	 * Sets the roll in radiants 
	 */
	public void setRoll(float roll) {
		if (this.roll == roll) return;
		this.roll = roll;

		rollMatrixDirty = true;
		modelMatrixDirty = true;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		if (this.z == z) return;
		this.z = z;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
	}

	/**
	 * sets the x, y and z properties
	 */
	public void setXYZ(float x, float y, float z) {
		if (this.x == x && this.y == y && this.z == z) return;
		this.x = x;
		this.y = y;
		this.z = z;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
	}

	/**
	 * sets the x, y and z properties
	 */
	public void setXYZ(Vector3f v) {
		if (this.x == v.getX() && this.y == v.getY() && this.z == v.getZ()) return;
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();

		positionMatrixDirty = true;
		modelMatrixDirty = true;
	}

	/**
	 * sets the x, y and z scale properties
	 */
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		if (this.scaleX == x && this.scaleY == y && this.scaleZ == z) return;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;

		maxScaleComponent = scaleX;
		if (scaleY > maxScaleComponent) maxScaleComponent = scaleY;
		if (scaleZ > maxScaleComponent) maxScaleComponent = scaleZ;

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getScaleZ() {
		return scaleZ;
	}

	public float getMaxScaleComponent() {
		return maxScaleComponent;
	}

	@Override
	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

}
