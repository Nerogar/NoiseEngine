package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Matrix4fUtils;
import de.nerogar.noise.util.Vector3f;

public class RenderProperties3f extends RenderProperties<RenderProperties3f> {

	private float yaw, pitch, roll;
	private float x, y, z;
	private float scaleX, scaleY, scaleZ, maxScaleComponent;
	private RenderProperties3f parent;

	private boolean  positionMatrixDirty = true;
	private Matrix4f positionMatrix;
	private boolean  scaleMatrixDirty    = true;
	private Matrix4f scaleMatrix;
	private boolean  yawMatrixDirty      = true;
	private Matrix4f yawMatrix;
	private boolean  pitchMatrixDirty    = true;
	private Matrix4f pitchMatrix;
	private boolean  rollMatrixDirty     = true;
	private Matrix4f rollMatrix;
	private int      modCount;
	private int      parentModCount;

	private boolean  modelMatrixDirty = true;
	private Matrix4f modelMatrix;
	private Matrix4f normalMatrix;

	private Matrix4f tempMatrix;

	public RenderProperties3f() {
		this(0, 0, 0, 0, 0, 0);
	}

	public RenderProperties3f(float yaw, float pitch, float roll, float x, float y, float z) {
		this(yaw, pitch, roll, x, y, z, 1, 1, 1);
	}

	public RenderProperties3f(float yaw, float pitch, float roll, float x, float y, float z, float scaleX, float scaleY, float scaleZ) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;

		init();
	}

	public RenderProperties3f(Matrix4f modelMatrix) {
		float x0 = modelMatrix.get(0, 0);
		float x1 = modelMatrix.get(1, 0);
		float x2 = modelMatrix.get(2, 0);

		float y0 = modelMatrix.get(0, 1);
		float y1 = modelMatrix.get(1, 1);
		float y2 = modelMatrix.get(2, 1);

		float z0 = modelMatrix.get(0, 2);
		float z1 = modelMatrix.get(1, 2);
		float z2 = modelMatrix.get(2, 2);

		// position
		x = modelMatrix.get(0, 3);
		y = modelMatrix.get(1, 3);
		z = modelMatrix.get(2, 3);

		// scale
		scaleX = (float) Math.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
		scaleY = (float) Math.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
		scaleZ = (float) Math.sqrt(z0 * z0 + z1 * z1 + z2 * z2);

		// rotation
		/*
		rotationMatrix

		= yaw*pitch*roll

		  ( E  0  F)   (1  0  0)   ( A  B  0)
		= ( 0  1  0) * (0  C  D) * (-B  A  0)
		  (-F  0  E)   (0 -D  C)   ( 0  0  1)

		  ( __  __  CF)
		= (-CB  CA  D )
		  ( __  __  CE)


		roll  = atan2(-CB, CA)
		yaw   = atan2(CF, CE)
		pitch = atan2(D, C)
			where C is calculated as
			C=CA/A = CA/cos(roll) if cos(roll) != 0
			C=CB/B = CB/sin(roll) if cos(roll) == 0

		 */

		roll = (float) Math.atan2(-x1, y1);
		yaw = (float) Math.atan2(z0, z2);

		double cosRoll = Math.cos(roll);
		if (Math.abs(cosRoll) > 0.01) {
			pitch = (float) Math.atan2(z1, y1 / cosRoll);
		} else {
			pitch = (float) Math.atan2(z1, (-x1) / Math.sin(roll));
		}

		init();
	}

	private void init() {
		positionMatrix = new Matrix4f();
		scaleMatrix = new Matrix4f();
		yawMatrix = new Matrix4f();
		pitchMatrix = new Matrix4f();
		rollMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();

		tempMatrix = new Matrix4f();

		maxScaleComponent = Math.max(scaleX, Math.max(scaleY, scaleZ));

		setPositionMatrix();
		setScaleMatrix();
		setYawMatrix();
		setPitchMatrix();
		setRollMatrix();
	}

	public void setParent(RenderProperties3f parent) {
		this.parent = parent;
		modelMatrixDirty = true;
		modCount++;
		parentModCount = parent.modCount;
	}

	private boolean hasParentChanged() {
		if (parent == null) return false;
		return parent.modCount != parentModCount || parent.hasParentChanged();
	}

	@Override
	public Matrix4f getModelMatrix() {
		if (modelMatrixDirty || hasParentChanged()) setModelMatrix();
		return modelMatrix;
	}

	public Matrix4f getNormalMatrix() {
		if (modelMatrixDirty || hasParentChanged()) setModelMatrix();
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

		tempMatrix.set(positionMatrix);
		tempMatrix.multiplyRight(yawMatrix);
		tempMatrix.multiplyRight(pitchMatrix);
		tempMatrix.multiplyRight(rollMatrix);

		//model matrix
		modelMatrix.set(tempMatrix).multiplyRight(scaleMatrix);
		if (parent != null) modelMatrix.multiplyLeft(parent.getModelMatrix());

		//normal matrix (rotations * scale^-1)
		scaleMatrix.set(0, 0, 1f / scaleX);
		scaleMatrix.set(1, 1, 1f / scaleY);
		scaleMatrix.set(2, 2, 1f / scaleZ);

		normalMatrix.set(tempMatrix).multiplyRight(scaleMatrix);
		if (parent != null) normalMatrix.multiplyLeft(parent.getNormalMatrix());

		//reset scale matrix
		scaleMatrix.set(0, 0, scaleX);
		scaleMatrix.set(1, 1, scaleY);
		scaleMatrix.set(2, 2, scaleZ);

		modelMatrixDirty = false;
		if (parent != null) parentModCount = parent.modCount;
		modCount++;
	}

	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Sets the yaw in radians
	 */
	public void setYaw(float yaw) {
		if (this.yaw == yaw) return;
		this.yaw = yaw;

		yawMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(false, true, false);
	}

	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Sets the pitch in radians
	 */
	public void setPitch(float pitch) {
		if (this.pitch == pitch) return;
		this.pitch = pitch;

		pitchMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(false, true, false);
	}

	/**
	 * @return the roll
	 */
	public float getRoll() {
		return roll;
	}

	/**
	 * Sets the roll in radians
	 */
	public void setRoll(float roll) {
		if (this.roll == roll) return;
		this.roll = roll;

		rollMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(false, true, false);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(true, false, false);
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(true, false, false);
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		if (this.z == z) return;
		this.z = z;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(true, false, false);
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
		modCount++;

		updateListener(true, false, false);
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
		modCount++;

		updateListener(true, false, false);
	}

	/**
	 * sets the x, y and z scale properties
	 */
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		if (this.scaleX == scaleX && this.scaleY == scaleY && this.scaleZ == scaleZ) return;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;

		maxScaleComponent = Math.max(scaleX, Math.max(scaleY, scaleZ));

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;

		updateListener(false, false, true);
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

	private void updateListener(boolean position, boolean rotation, boolean scale) {
		if (listener != null) {
			listener.update(this, position, rotation, scale);
		}
	}

	@Override
	public String toString() {
		return "RenderProperties3f{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", yaw=" + yaw +
				", pitch=" + pitch +
				", roll=" + roll +
				", scaleX=" + scaleX +
				", scaleY=" + scaleY +
				", scaleZ=" + scaleZ +
				", visible=" + isVisible() +
				'}';
	}

}
