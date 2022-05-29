package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.*;

public class Transformation implements ITransformation {

	private static final float PI = (float) Math.PI;

	protected float x, y, z;
	protected float yaw, pitch, roll;
	protected float scaleX, scaleY, scaleZ;
	protected ITransformation parent;

	protected int modCount;
	protected int parentModCount;

	protected boolean   positionMatrixDirty = true;
	protected IMatrix4f positionMatrix;
	protected boolean   rotationMatrixDirty = true;
	protected IMatrix4f rotationMatrix;
	protected boolean   scaleMatrixDirty    = true;
	protected IMatrix4f scaleMatrix;

	protected boolean   modelMatrixDirty = true;
	protected IMatrix4f modelMatrix;
	protected IMatrix4f normalMatrix;

	protected IMatrix4f tempMatrix;

	public Transformation(float x, float y, float z, float yaw, float pitch, float roll, float scaleX, float scaleY, float scaleZ) {
		setPosition(x, y, z);
		setRotation(yaw, pitch, roll);
		setScale(scaleX, scaleY, scaleZ);

		init();
	}

	public Transformation(float x, float y, float z, float yaw, float pitch, float roll) {
		this(x, y, z, yaw, pitch, roll, 1, 1, 1);
	}

	public Transformation(float x, float y, float z) {
		this(x, y, z, 0, 0, 0);
	}

	public Transformation() {
		this(0, 0, 0);
	}

	public Transformation(IReadonlyMatrix4f modelMatrix) {
		setFromMatrix(modelMatrix);
		init();
	}

	private void init() {
		positionMatrix = new Matrix4f();
		scaleMatrix = new Matrix4f();
		rotationMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		normalMatrix = new Matrix4f();

		tempMatrix = new Matrix4f();

		setPositionMatrix();
		setScaleMatrix();
		setRotationMatrix();
	}

	// position

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setZ(float z) {
		if (this.z == z) return;
		this.z = z;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setPosition(float x, float y, float z) {
		if (this.x == x && this.y == y && this.z == z) return;
		this.x = x;
		this.y = y;
		this.z = z;

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setPosition(IReadonlyVector3f v) {
		if (this.x == v.getX() && this.y == v.getY() && this.z == v.getZ()) return;
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();

		positionMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	// rotation

	@Override
	public float getYaw() {
		return yaw;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	public float getRoll() {
		return roll;
	}

	@Override
	public void setYaw(float yaw) {
		if (this.yaw == yaw) return;
		this.yaw = yaw;

		rotationMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setPitch(float pitch) {
		if (this.pitch == pitch) return;
		this.pitch = pitch;

		rotationMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setRoll(float roll) {
		if (this.roll == roll) return;
		this.roll = roll;

		rotationMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setRotation(float yaw, float pitch, float roll) {
		if (this.yaw == yaw && this.pitch == pitch && this.roll == roll) return;
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;

		rotationMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setLookAt(float x, float y, float z) {
		float lookVecX = x - this.x;
		float lookVecY = y - this.y;
		float lookVecZ = z - this.z;
		setLookDirection(lookVecX, lookVecY, lookVecZ);
	}

	@Override
	public void setLookAt(IReadonlyVector3f lookAt) {
		setLookAt(lookAt.getX(), lookAt.getY(), lookAt.getZ());
	}

	@Override
	public void setLookDirection(float dirX, float dirY, float dirZ) {
		if (dirZ == 0) {
			float yaw = (dirX < 0 ? PI / 2f : -PI / 2f);
			if (this.yaw != yaw) {
				this.yaw = yaw;
				rotationMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		} else {
			float sign = dirZ > 0 ? PI : 0;
			float yaw = (float) Math.atan(dirX / dirZ) + sign;
			if (this.yaw != yaw) {
				this.yaw = yaw;
				rotationMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		}

		if (dirX == 0 && dirZ == 0) {
			float pitch = dirY > 0 ? PI / 2f : -PI / 2f;
			if (this.pitch != pitch) {
				this.pitch = pitch;
				rotationMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		} else {
			float lengthXZ = (float) Math.sqrt(dirX * dirX + dirZ * dirZ);
			float pitch = (float) Math.atan(dirY / lengthXZ);
			if (this.pitch != pitch) {
				this.pitch = pitch;
				rotationMatrixDirty = true;
				modelMatrixDirty = true;
				modCount++;
			}
		}
	}

	@Override
	public void setLookDirection(IReadonlyVector3f lookADir) {
		setLookDirection(lookADir.getX(), lookADir.getY(), lookADir.getZ());
	}

	// scale

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public float getScaleZ() {
		return scaleZ;
	}

	@Override
	public void setScaleX(float scaleX) {
		if (this.scaleX == scaleX) return;
		this.scaleX = scaleX;

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setScaleY(float scaleY) {
		if (this.scaleY == scaleY) return;
		this.scaleY = scaleY;

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setScaleZ(float scaleZ) {
		if (this.scaleZ == scaleZ) return;
		this.scaleZ = scaleZ;

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		if (this.scaleX == scaleX && this.scaleY == scaleY && this.scaleZ == scaleZ) return;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	@Override
	public void setScale(IReadonlyVector3f scale) {
		if (this.scaleX == scale.getX() && this.scaleY == scale.getY() && this.scaleZ == scale.getZ()) return;
		this.scaleX = scale.getX();
		this.scaleY = scale.getY();
		this.scaleZ = scale.getZ();

		scaleMatrixDirty = true;
		modelMatrixDirty = true;
		modCount++;
	}

	// misc

	@Override
	public void setFromMatrix(IReadonlyMatrix4f modelMatrix) {
		modelMatrixDirty = true;
		positionMatrixDirty = true;
		scaleMatrixDirty = true;
		rotationMatrixDirty = true;
		modCount++;

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
			pitch = (float) -Math.atan2(z1, y1 / cosRoll);
		} else {
			pitch = (float) -Math.atan2(z1, (-x1) / Math.sin(roll));
		}
	}

	// parent

	@Override
	public ITransformation getParent() {
		return parent;
	}

	@Override
	public void setParent(ITransformation parent) {
		this.parent = parent;
		modelMatrixDirty = true;
		modCount++;

		if (parent != null) {
			parentModCount = parent.getModCount();
		}
	}

	@Override
	public int getModCount() {
		return modCount;
	}

	@Override
	public boolean hasParentChanged() {
		if (parent == null) return false;
		return parent.getModCount() != parentModCount || parent.hasParentChanged();
	}

	// output matrices

	public IMatrix4f getModelMatrix() {
		if (modelMatrixDirty || hasParentChanged()) setModelMatrix();
		return modelMatrix;
	}

	public IMatrix4f getNormalMatrix() {
		if (modelMatrixDirty || hasParentChanged()) setModelMatrix();
		return normalMatrix;
	}

	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////

	protected void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, x, y, z);
		positionMatrixDirty = false;
	}

	protected void setScaleMatrix() {
		Matrix4fUtils.setScaleMatrix(scaleMatrix, scaleX, scaleY, scaleZ);
		scaleMatrixDirty = false;
	}

	protected void setRotationMatrix() {
		Matrix4fUtils.setRotationMatrix(rotationMatrix, yaw, pitch, roll);
		rotationMatrixDirty = false;
	}

	protected void setModelMatrix() {
		if (positionMatrixDirty) setPositionMatrix();
		if (scaleMatrixDirty) setScaleMatrix();
		if (rotationMatrixDirty) setRotationMatrix();

		tempMatrix.set(positionMatrix);
		tempMatrix.multiplyRight(rotationMatrix);

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
		if (parent != null) parentModCount = parent.getModCount();
		modCount++;
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
