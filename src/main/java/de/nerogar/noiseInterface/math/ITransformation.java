package de.nerogar.noiseInterface.math;

public interface ITransformation extends IReadOnlyTransformation {

	// position

	/**
	 * Sets the x component of the position
	 */
	void setX(float x);

	/**
	 * Sets the y component of the position
	 */
	void setY(float y);

	/**
	 * Sets the z component of the position
	 */
	void setZ(float z);

	/**
	 * Sets the position
	 */
	void setPosition(float x, float y, float z);

	/**
	 * Sets the position
	 */
	void setPosition(IReadonlyVector3f position);

	// rotation

	/**
	 * Sets the yaw component of the rotation in radians
	 */
	void setYaw(float yaw);

	/**
	 * Sets the pitch component of the rotation in radians
	 */
	void setPitch(float pitch);

	/**
	 * Sets the roll component of the rotation in radians
	 */
	void setRoll(float roll);

	/**
	 * Sets rotation in radians
	 */
	void setRotation(float yaw, float pitch, float roll);

	/**
	 * Sets the rotation such that the local negative z axis points at the point (x, y, z)
	 *
	 * @param x the x coordinate to look at
	 * @param y the y coordinate to look at
	 * @param z the z coordinate to look at
	 */
	void setLookAt(float x, float y, float z);

	void setLookAt(IReadonlyVector3f lookAt);

	/**
	 * Sets the rotation such that the local negative z axis points in the direction (dirX, dirZ, dirZ)
	 *
	 * @param dirX the x direction to look at
	 * @param dirY the y direction to look at
	 * @param dirZ the z direction to look at
	 */
	void setLookDirection(float dirX, float dirY, float dirZ);

	void setLookDirection(IReadonlyVector3f lookADir);

	// scale

	/**
	 * Sets the x component of the scale
	 */
	void setScaleX(float scaleX);

	/**
	 * Sets the y component of the scale
	 */
	void setScaleY(float scaleY);

	/**
	 * Sets the z component of the scale
	 */
	void setScaleZ(float scaleZ);

	/**
	 * Sets the scale
	 */
	void setScale(float scaleX, float scaleY, float scaleZ);

	/**
	 * Sets the scale
	 */
	void setScale(IReadonlyVector3f scale);

	// misc

	void setFromMatrix(IReadonlyMatrix4f matrix);

	// parent

	void setParent(ITransformation parent);

	// output matrices

}
