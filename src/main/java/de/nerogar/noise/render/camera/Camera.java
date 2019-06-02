package de.nerogar.noise.render.camera;

public abstract class Camera implements IReadOnlyCamera {

	public abstract void setAspect(float aspect);

	protected abstract void setUnitRays();

	protected abstract void setProjectionMatrix();

	/**
	 * Sets the camera yaw in radians
	 */
	public abstract void setYaw(float yaw);

	/**
	 * Sets the camera pitch in radians
	 */
	public abstract void setPitch(float pitch);

	/**
	 * Sets the camera roll in radians
	 */
	public abstract void setRoll(float roll);

	public abstract void setX(float x);

	public abstract void setY(float y);

	public abstract void setZ(float z);

	public abstract void setXYZ(float x, float y, float z);

	public abstract void setLookAt(float lookX, float lookY, float lookZ);

}
