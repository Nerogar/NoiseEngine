package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IVector3f;

public class Transformation {

	private static final float PI = (float) Math.PI;

	protected float yaw, pitch, roll;
	protected float x, y, z;
	protected float scaleX, scaleY, scaleZ, maxScaleComponent;
	protected Transformation parent;

	protected boolean   positionMatrixDirty = true;
	protected IMatrix4f positionMatrix;
	protected boolean   scaleMatrixDirty    = true;
	protected IMatrix4f scaleMatrix;
	protected boolean   yawMatrixDirty      = true;
	protected IMatrix4f yawMatrix;
	protected boolean   pitchMatrixDirty    = true;
	protected IMatrix4f pitchMatrix;
	protected boolean   rollMatrixDirty     = true;
	protected IMatrix4f rollMatrix;
	protected int       modCount;
	protected int       parentModCount;

	protected boolean   modelMatrixDirty = true;
	protected IMatrix4f modelMatrix;
	protected IMatrix4f normalMatrix;

	private IMatrix4f tempMatrix;

	protected RenderPropertiesListener listener;

	public interface RenderPropertiesListener {

		void update(Transformation transformation, boolean position, boolean rotation, boolean scale);
	}

	public Transformation() {
		this(0, 0, 0, 0, 0, 0);
	}

	public Transformation(float yaw, float pitch, float roll, float x, float y, float z) {
		this(yaw, pitch, roll, x, y, z, 1, 1, 1);
	}

	public Transformation(float yaw, float pitch, float roll, float x, float y, float z, float scaleX, float scaleY, float scaleZ) {
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

	public Transformation(IMatrix4f modelMatrix) {
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

	public void setParent(Transformation parent) {
		this.parent = parent;
		modelMatrixDirty = true;
		modCount++;
		parentModCount = parent.modCount;
	}

	private boolean hasParentChanged() {
		if (parent == null) return false;
		return parent.modCount != parentModCount || parent.hasParentChanged();
	}

	public IMatrix4f getModelMatrix() {
		if (modelMatrixDirty || hasParentChanged()) setModelMatrix();
		return modelMatrix;
	}

	public IMatrix4f getNormalMatrix() {
		if (modelMatrixDirty || hasParentChanged()) setModelMatrix();
		return normalMatrix;
	}

	protected void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, x, y, z);
		positionMatrixDirty = false;
	}

	protected void setScaleMatrix() {
		Matrix4fUtils.setScaleMatrix(scaleMatrix, scaleX, scaleY, scaleZ);
		scaleMatrixDirty = false;
	}

	protected void setYawMatrix() {
		Matrix4fUtils.setYawMatrix(yawMatrix, yaw);
		yawMatrixDirty = false;
	}

	protected void setPitchMatrix() {
		Matrix4fUtils.setPitchMatrix(pitchMatrix, pitch);
		pitchMatrixDirty = false;
	}

	protected void setRollMatrix() {
		Matrix4fUtils.setRollMatrix(rollMatrix, roll);
		rollMatrixDirty = false;
	}

	protected void setModelMatrix() {
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

	/**
	 * Sets the rotation such that the local negative z axis points at the point (x, y, z)
	 *
	 * @param lookX the x coordinate to look at
	 * @param lookY the y coordinate to look at
	 * @param lookZ the z coordinate to look at
	 */
	public void setLookAt(float lookX, float lookY, float lookZ) {
		float lookVecX = lookX - x;
		float lookVecY = lookY - y;
		float lookVecZ = lookZ - z;
		setLookDirection(lookVecX, lookVecY, lookVecZ);
	}

	/**
	 * Sets the rotation such that the local negative z axis points in the direction (x, y, z)
	 *
	 * @param lookVecX the x direction to look at
	 * @param lookVecY the y direction to look at
	 * @param lookVecZ the z direction to look at
	 */
	public void setLookDirection(float lookVecX, float lookVecY, float lookVecZ) {
		int oldModCount = modCount;

		if (lookVecZ == 0) {
			float yaw = (lookVecX < 0 ? PI / 2f : -PI / 2f);
			if (this.yaw != yaw) {
				this.yaw = yaw;
				yawMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		} else {
			float sign = lookVecZ > 0 ? PI : 0;
			float yaw = (float) Math.atan(lookVecX / lookVecZ) + sign;
			if (this.yaw != yaw) {
				this.yaw = yaw;
				yawMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		}

		if (lookVecX == 0 && lookVecZ == 0) {
			float pitch = lookVecY > 0 ? PI / 2f : -PI / 2f;
			if (this.pitch != pitch) {
				this.pitch = pitch;
				pitchMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		} else {
			float lengthXZ = (float) Math.sqrt(lookVecX * lookVecX + lookVecZ * lookVecZ);
			float pitch = (float) Math.atan(lookVecY / lengthXZ);
			if (this.pitch != pitch) {
				this.pitch = pitch;
				pitchMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		}

		if (modCount != oldModCount) {
			updateListener(false, true, false);
		}
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
	public void setXYZ(IVector3f v) {
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

	/**
	 * Set the listener for this renderProperties instance.
	 * Only one listener can be attached.
	 * Call {@code setListener(null)} to clear the listener
	 *
	 * @param listener the new listener
	 */
	public void setListener(RenderPropertiesListener listener) {
		this.listener = listener;
	}

	private void updateListener(boolean position, boolean rotation, boolean scale) {
		if (listener != null) {
			listener.update(this, position, rotation, scale);
		}
	}

	public int getModCount() {
		return modCount;
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
				'}';
	}

}
