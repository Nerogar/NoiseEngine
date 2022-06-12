package de.nerogar.noiseInterface.math;

public interface IReadOnlyTransformation {

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
	 * @return the effective x component of the position in world space, including parent transformations
	 */
	float getEffectiveX();

	/**
	 * @return the effective y component of the position in world space, including parent transformations
	 */
	float getEffectiveY();

	/**
	 * @return the effective z component of the position in world space, including parent transformations
	 */
	float getEffectiveZ();

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

	ITransformation getParent();

	int getModCount();

	boolean hasParentChanged();

	IMatrix4f getModelMatrix();

	IMatrix4f getNormalMatrix();
}
