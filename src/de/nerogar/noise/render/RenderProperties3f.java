package de.nerogar.noise.render;

import de.nerogar.noise.util.*;

public class RenderProperties3f implements RenderProperties {

	private float yaw, pitch, roll;
	private float x, y, z;
	private float scaleX, scaleY, scaleZ;

	private Matrix4f positionMatrix;
	private Matrix4f scaleMatrix;
	private Matrix4f yawMatrix;
	private Matrix4f pitchMatrix;
	private Matrix4f rollMatrix;

	private boolean finalMatrixDirty = true;
	private Matrix4f finalMatrix;

	public RenderProperties3f() {
		this(0, 0, 0, 0, 0, 0);
	}

	public RenderProperties3f(float yaw, float pitch, float roll, float x, float y, float z) {
		positionMatrix = new Matrix4f();
		scaleMatrix = new Matrix4f();
		yawMatrix = new Matrix4f();
		pitchMatrix = new Matrix4f();
		rollMatrix = new Matrix4f();
		finalMatrix = new Matrix4f();

		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.x = x;
		this.y = y;
		this.z = z;

		scaleX = 1.0f;
		scaleY = 1.0f;
		scaleZ = 1.0f;

		setPositionMatrix();
		setScaleMatrix();
		setYawMatrix();
		setPitchMatrix();
		setRollMatrix();
	}

	@Override
	public Matrix4f getModelMatrix() {
		if (finalMatrixDirty) setFinalMatrix();
		return finalMatrix;
	}

	private void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, x, y, z);
		finalMatrixDirty = true;
	}

	private void setScaleMatrix() {
		Matrix4fUtils.setScaleMatrix(scaleMatrix, scaleX, scaleY, scaleZ);
		finalMatrixDirty = true;
	}

	private void setYawMatrix() {
		Matrix4fUtils.setYawMatrix(yawMatrix, yaw);
		finalMatrixDirty = true;
	}

	private void setPitchMatrix() {
		Matrix4fUtils.setPitchMatrix(pitchMatrix, pitch);
		finalMatrixDirty = true;
	}

	private void setRollMatrix() {
		Matrix4fUtils.setRollMatrix(rollMatrix, roll);
		finalMatrixDirty = true;
	}

	private void setFinalMatrix() {
		finalMatrix.set(positionMatrix);
		finalMatrix.multiplyLeft(scaleMatrix);
		finalMatrix.multiplyLeft(yawMatrix);
		finalMatrix.multiplyLeft(pitchMatrix);
		finalMatrix.multiplyLeft(rollMatrix);

		finalMatrixDirty = false;
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
		setYawMatrix();
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
		setPitchMatrix();
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
		setRollMatrix();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;
		setPositionMatrix();
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;
		setPositionMatrix();
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		if (this.z == z) return;
		this.z = z;
		setPositionMatrix();
	}

	/**
	 * sets the x, y and z properties
	 */
	public void setXYZ(float x, float y, float z) {
		if (this.x == x && this.y == y && this.z == z) return;
		this.x = x;
		this.y = y;
		this.z = z;
		setPositionMatrix();
	}

	/**
	 * sets the x, y and z properties
	 */
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		if (this.scaleX == x && this.scaleY == y && this.scaleZ == z) return;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		setScaleMatrix();
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

	/**
	 * sets the x, y and z properties
	 */
	public void setXYZ(Vector3f v) {
		if (this.x == v.getX() && this.y == v.getY() && this.z == v.getZ()) return;
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();
		setPositionMatrix();
	}

}
