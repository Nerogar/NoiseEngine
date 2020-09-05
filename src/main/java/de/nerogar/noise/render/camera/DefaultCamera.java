package de.nerogar.noise.render.camera;

import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Matrix4fUtils;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.render.IViewRegion;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IVector3f;

public abstract class DefaultCamera extends Camera {

	protected static final float PI = (float) Math.PI;

	protected float yaw, pitch, roll;
	protected float x, y, z;

	protected IMatrix4f positionMatrix;
	protected IMatrix4f yawMatrix;
	protected IMatrix4f pitchMatrix;
	protected IMatrix4f rollMatrix;

	protected boolean   viewMatrixDirty = true;
	protected IMatrix4f viewMatrix;

	protected boolean   projectionMatrixDirty = true;
	protected IMatrix4f projectionMatrix;

	protected IVector3f directionRight;
	protected IVector3f directionUp;
	protected IVector3f directionAt;

	protected Ray   unitRayRight;
	protected Ray   unitRayTop;
	protected Ray   unitRayCenter;
	protected float unitSize;

	protected IViewRegion viewRegion;

	private final IReadOnlyCamera[] cameraAsArray;

	public DefaultCamera(IViewRegion viewRegion) {
		cameraAsArray = new IReadOnlyCamera[] { this };

		positionMatrix = new Matrix4f();
		yawMatrix = new Matrix4f();
		pitchMatrix = new Matrix4f();
		rollMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();

		directionRight = new Vector3f();
		directionUp = new Vector3f();
		directionAt = new Vector3f();

		unitRayRight = new Ray(new Vector3f(), new Vector3f());
		unitRayTop = new Ray(new Vector3f(), new Vector3f());
		unitRayCenter = new Ray(new Vector3f(), new Vector3f());

		projectionMatrix = new Matrix4f();

		this.viewRegion = viewRegion;

		setPositionMatrix();
		setYawMatrix();
		setPitchMatrix();
		setRollMatrix();
	}

	@Override
	public IReadOnlyCamera[] cameras() { return cameraAsArray; }

	public abstract void setAspect(float aspect);

	public abstract float getAspect();

	private void setPositionMatrix() {
		Matrix4fUtils.setPositionMatrix(positionMatrix, -x, -y, -z);
		viewMatrixDirty = true;
	}

	private void setYawMatrix() {
		Matrix4fUtils.setYawMatrix(yawMatrix, -yaw);
		viewMatrixDirty = true;
	}

	private void setPitchMatrix() {
		Matrix4fUtils.setPitchMatrix(pitchMatrix, -pitch);
		viewMatrixDirty = true;
	}

	private void setRollMatrix() {
		Matrix4fUtils.setRollMatrix(rollMatrix, -roll);
		viewMatrixDirty = true;
	}

	private void setViewMatrix() {
		viewMatrixDirty = false;

		viewMatrix.set(positionMatrix);
		viewMatrix.multiplyLeft(yawMatrix);
		viewMatrix.multiplyLeft(pitchMatrix);
		viewMatrix.multiplyLeft(rollMatrix);

		setDirections();

		viewRegion.setPlanes(this);
	}

	protected abstract void setUnitRays();

	private void setDirections() {
		// world space directions
		directionRight.set(1.0f, 0.0f, 0.0f);
		directionToWorldSpace(directionRight);

		directionUp.set(0.0f, 1.0f, 0.0f);
		directionToWorldSpace(directionUp);

		directionAt.set(0.0f, 0.0f, -1.0f);
		directionToWorldSpace(directionAt);

		// unit distance rays
		setUnitRays();
	}

	public IMatrix4f getViewMatrix() {
		if (viewMatrixDirty) setViewMatrix();

		return viewMatrix;
	}

	protected abstract void setProjectionMatrix();

	public IMatrix4f getProjectionMatrix() {
		if (projectionMatrixDirty) setProjectionMatrix();

		return projectionMatrix;
	}

	public IViewRegion getViewRegion() {
		if (viewMatrixDirty) setViewMatrix();
		if (projectionMatrixDirty) setProjectionMatrix();

		return viewRegion;
	}

	/**
	 * Sets the camera yaw in radiants
	 */
	public void setYaw(float yaw) {
		if (this.yaw == yaw) return;
		this.yaw = yaw;
		setYawMatrix();
	}

	/**
	 * @return The camera yaw in radiants
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Sets the camera pitch in radiants
	 */
	public void setPitch(float pitch) {
		if (this.pitch == pitch) return;
		this.pitch = pitch;
		setPitchMatrix();
	}

	/**
	 * @return The camera pitch in radiants
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Sets the camera roll in radiants
	 */
	public void setRoll(float roll) {
		if (this.roll == roll) return;
		this.roll = roll;
		setRollMatrix();
	}

	/**
	 * @return The camera roll in radiants
	 */
	public float getRoll() {
		return roll;
	}

	public void setX(float x) {
		if (this.x == x) return;
		this.x = x;
		setPositionMatrix();
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		if (this.y == y) return;
		this.y = y;
		setPositionMatrix();
	}

	public float getY() {
		return y;
	}

	public void setZ(float z) {
		if (this.z == z) return;
		this.z = z;
		setPositionMatrix();
	}

	public float getZ() {
		return z;
	}

	public void setXYZ(float x, float y, float z) {
		if (this.x == x && this.y == y && this.z == z) return;
		this.x = x;
		this.y = y;
		this.z = z;
		setPositionMatrix();
	}

	public void setLookAt(float lookX, float lookY, float lookZ) {
		float lookVecX = lookX - x;
		float lookVecY = lookY - y;
		float lookVecZ = lookZ - z;

		if (lookVecZ == 0) {
			setYaw(lookVecX > 0 ? PI / 2f : -PI / 2f);
		} else {
			float sign = lookVecZ > 0 ? PI : 0;

			setYaw((float) Math.atan(lookVecX / lookVecZ) + sign);
		}

		if (lookVecX == 0 && lookVecZ == 0) {
			setPitch(lookVecY > 0 ? PI / 2f : -PI / 2f);
		} else {
			float lengthXZ = (float) Math.sqrt(lookVecX * lookVecX + lookVecZ * lookVecZ);

			setPitch((float) Math.atan(lookVecY / lengthXZ));
		}
	}

	/**
	 * Transform a point in world space to view space.
	 *
	 * @param point the point to transform
	 */
	public void pointToViewSpace(IVector3f point) {
		float newX, newY, newZ;

		newX = point.getX() * getViewMatrix().get(0, 0);
		newY = point.getX() * viewMatrix.get(1, 0);
		newZ = point.getX() * viewMatrix.get(2, 0);

		newX += point.getY() * viewMatrix.get(0, 1);
		newY += point.getY() * viewMatrix.get(1, 1);
		newZ += point.getY() * viewMatrix.get(2, 1);

		newX += point.getZ() * viewMatrix.get(0, 2);
		newY += point.getZ() * viewMatrix.get(1, 2);
		newZ += point.getZ() * viewMatrix.get(2, 2);

		newX += viewMatrix.get(0, 3);
		newY += viewMatrix.get(1, 3);
		newZ += viewMatrix.get(2, 3);

		point.setX(newX);
		point.setY(newY);
		point.setZ(newZ);
	}

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	public void directionToViewSpace(IVector3f direction) {
		float newX, newY, newZ;

		newX = direction.getX() * getViewMatrix().get(0, 0);
		newY = direction.getX() * viewMatrix.get(1, 0);
		newZ = direction.getX() * viewMatrix.get(2, 0);

		newX += direction.getY() * viewMatrix.get(0, 1);
		newY += direction.getY() * viewMatrix.get(1, 1);
		newZ += direction.getY() * viewMatrix.get(2, 1);

		newX += direction.getZ() * viewMatrix.get(0, 2);
		newY += direction.getZ() * viewMatrix.get(1, 2);
		newZ += direction.getZ() * viewMatrix.get(2, 2);

		direction.setX(newX);
		direction.setY(newY);
		direction.setZ(newZ);
	}

	/**
	 * Transform a point in view space to world space.
	 *
	 * @param point the point to transform
	 */
	public void pointToWorldSpace(IVector3f point) {
		float newX, newY, newZ;

		point.addX(-getViewMatrix().get(0, 3));
		point.addY(-viewMatrix.get(1, 3));
		point.addZ(-viewMatrix.get(2, 3));

		newX = point.getX() * viewMatrix.get(0, 0);
		newY = point.getX() * viewMatrix.get(0, 1);
		newZ = point.getX() * viewMatrix.get(0, 2);

		newX += point.getY() * viewMatrix.get(1, 0);
		newY += point.getY() * viewMatrix.get(1, 1);
		newZ += point.getY() * viewMatrix.get(1, 2);

		newX += point.getZ() * viewMatrix.get(2, 0);
		newY += point.getZ() * viewMatrix.get(2, 1);
		newZ += point.getZ() * viewMatrix.get(2, 2);

		point.setX(newX);
		point.setY(newY);
		point.setZ(newZ);
	}

	/**
	 * Transform a direction in world space to view space.
	 *
	 * @param direction the direction to transform
	 */
	public void directionToWorldSpace(IVector3f direction) {
		float newX, newY, newZ;

		newX = direction.getX() * getViewMatrix().get(0, 0);
		newY = direction.getX() * viewMatrix.get(0, 1);
		newZ = direction.getX() * viewMatrix.get(0, 2);

		newX += direction.getY() * viewMatrix.get(1, 0);
		newY += direction.getY() * viewMatrix.get(1, 1);
		newZ += direction.getY() * viewMatrix.get(1, 2);

		newX += direction.getZ() * viewMatrix.get(2, 0);
		newY += direction.getZ() * viewMatrix.get(2, 1);
		newZ += direction.getZ() * viewMatrix.get(2, 2);

		direction.setX(newX);
		direction.setY(newY);
		direction.setZ(newZ);
	}

	/**
	 * <p>
	 * Unproject a single pixel to a ray in world space.
	 * The pixel coordinates are in normalized screen coordinates.
	 * (0, 0) is the center, (1, 1) is the top right.
	 * </p>
	 * <p>
	 * The ray is in the following format: ray.pos + ray.dir has a z value of 1 in screen space.
	 * </p>
	 *
	 * @param x the x component of the pixel
	 * @param y the y component of the pixel
	 * @return the ray in world space
	 */
	public Ray unproject(float x, float y) {
		IVector3f start = new Vector3f(
				unitRayCenter.getStart().getX() + unitRayRight.getStart().getX() * x + unitRayTop.getStart().getX() * y,
				unitRayCenter.getStart().getY() + unitRayRight.getStart().getY() * x + unitRayTop.getStart().getY() * y,
				unitRayCenter.getStart().getZ() + unitRayRight.getStart().getZ() * x + unitRayTop.getStart().getZ() * y
		);

		IVector3f dir = new Vector3f(
				unitRayCenter.getDir().getX() + unitRayRight.getDir().getX() * x + unitRayTop.getDir().getX() * y,
				unitRayCenter.getDir().getY() + unitRayRight.getDir().getY() * x + unitRayTop.getDir().getY() * y,
				unitRayCenter.getDir().getZ() + unitRayRight.getDir().getZ() * x + unitRayTop.getDir().getZ() * y
		);

		return new Ray(start, dir);
	}

	/**
	 * Calculate the length a line from (0, 0, -1) to (0, 1, -1) in view space would have in screen space
	 *
	 * @return the unit zize
	 */
	public float getUnitSize() {
		return unitSize;
	}

	public IVector3f getDirectionRight() {
		if (viewMatrixDirty) setViewMatrix();

		return directionRight;
	}

	public IVector3f getDirectionUp() {
		if (viewMatrixDirty) setViewMatrix();

		return directionUp;
	}

	public IVector3f getDirectionAt() {
		if (viewMatrixDirty) setViewMatrix();

		return directionAt;
	}

	/**
	 * returns the equivalent of "unproject(1, 0)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	public Ray getUnitRayRight() {
		if (viewMatrixDirty) setViewMatrix();

		return unitRayRight;
	}

	/**
	 * returns the equivalent of "unproject(0, 1)-unproject(0, 0)"
	 *
	 * @return the ray
	 */
	public Ray getUnitRayTop() {
		if (viewMatrixDirty) setViewMatrix();

		return unitRayTop;
	}

	/**
	 * returns the same as unproject(0, 0)
	 *
	 * @return the ray
	 */
	public Ray getUnitRayCenter() {
		if (viewMatrixDirty) setViewMatrix();

		return unitRayCenter;
	}

}
