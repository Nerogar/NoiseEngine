package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Matrix4fUtils;
import de.nerogar.noise.util.Vector2f;

public class RenderProperties2f extends RenderProperties<RenderProperties2f> {

	private float roll;
	private float x, y;
	private float scaleX, scaleY;

	private Matrix4f positionMatrix;
	private Matrix4f scaleMatrix;
	private Matrix4f rollMatrix;

	private boolean finalMatrixDirty = true;
	private Matrix4f finalMatrix;

	public RenderProperties2f() {
		this(0, 0, 0);
	}

	public RenderProperties2f(float roll, float x, float y) {
		positionMatrix = new Matrix4f();
		scaleMatrix = new Matrix4f();
		rollMatrix = new Matrix4f();
		finalMatrix = new Matrix4f();

		this.roll = roll;
		this.x = x;
		this.y = y;

		scaleX = 1.0f;
		scaleY = 1.0f;

		setPositionMatrix();
		setScaleMatrix();
		setRollMatrix();
	}

	@Override
	public Matrix4f getModelMatrix() {
		if (finalMatrixDirty) setFinalMatrix();
		return finalMatrix;
	}

	private void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, x, y, 0.0f);
		finalMatrixDirty = true;
	}

	private void setScaleMatrix() {
		Matrix4fUtils.setScaleMatrix(scaleMatrix, scaleX, scaleY, 1.0f);
		finalMatrixDirty = true;
	}

	private void setRollMatrix() {
		Matrix4fUtils.setRollMatrix(rollMatrix, roll);
		finalMatrixDirty = true;
	}

	private void setFinalMatrix() {
		finalMatrix.set(scaleMatrix);
		finalMatrix.multiplyLeft(rollMatrix);
		finalMatrix.multiplyLeft(positionMatrix);

		finalMatrixDirty = false;
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

		updateListener(false, true, false);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;
		setPositionMatrix();

		updateListener(true, false, false);
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;
		setPositionMatrix();

		updateListener(true, false, false);
	}

	/**
	 * sets the x, y properties
	 */
	public void setXY(float x, float y) {
		if (this.x == x && this.y == y) return;
		this.x = x;
		this.y = y;
		setPositionMatrix();

		updateListener(true, false, false);
	}

	/**
	 * sets the x, y properties
	 */
	public void setXYZ(Vector2f v) {
		if (this.x == v.getX() && this.y == v.getY()) return;
		this.x = v.getX();
		this.y = v.getY();
		setPositionMatrix();

		updateListener(true, false, false);
	}

	/**
	 * sets the x, y properties
	 */
	public void setScale(float scaleX, float scaleY) {
		if (this.scaleX == scaleX && this.scaleY == scaleY) return;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		setScaleMatrix();

		updateListener(false, false, true);
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	private void updateListener(boolean position, boolean rotation, boolean scale) {
		if (listener != null) {
			listener.update(this, position, rotation, scale);
		}
	}

}
