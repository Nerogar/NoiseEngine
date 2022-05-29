package de.nerogar.noiseInterface.math;

public interface ITransformation {

	// position

	/**
	 * @return the x component of the position
	 */
	float getX();

	/**
	 * @return the y component of the position
	 */
	float getY();

	/**
	 * @return the z component of the position
	 */
	float getZ();

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
	 * @return the yaw component of the rotation in radians
	 */
	float getYaw();

	/**
	 * @return the pitch component of the rotation in radians
	 */
	float getPitch();

	/**
	 * @return the roll component of the rotation in radians
	 */
	float getRoll();

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
	 * @return the x component of the scale
	 */
	float getScaleX();

	/**
	 * @return the y component of the scale
	 */
	float getScaleY();

	/**
	 * @return the z component of the scale
	 */
	float getScaleZ();

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

	ITransformation getParent();

	void setParent(ITransformation parent);

	int getModCount();

	boolean hasParentChanged();

	// output matrices

	IMatrix4f getModelMatrix();

	IMatrix4f getNormalMatrix();

}
