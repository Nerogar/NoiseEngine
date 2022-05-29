package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.*;

public class BoundingSphere implements IBounding {

	private IVector3f       center;
	private float           radius;
	private ITransformation transformation;

	/**
	 * create a new bounding in the shape of a sphere
	 *
	 * @param center the center of the sphere
	 * @param radius the radius of the sphere
	 */
	public BoundingSphere(IVector3f center, float radius) {
		this.center = center;
		this.radius = radius;
	}

	/**
	 * getter for the center of the Sphere
	 *
	 * @return the center
	 */
	public IVector3f getCenter() {
		return center;
	}

	/**
	 * setter for the center of the sphere
	 *
	 * @param center the new center of the sphere
	 */
	public void setCenter(IVector3f center) {
		this.center.set(center);
	}

	/**
	 * setter for the center of the sphere
	 *
	 * @param x the new x component of the center of the sphere
	 * @param y the new y component of the center of the sphere
	 * @param z the new z component of the center of the sphere
	 */
	public void setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
	}

	/**
	 * getter for the radius of the Sphere
	 *
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * setter for the radius of the sphere
	 *
	 * @param radius the new radius of the sphere
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public void setTransformation(ITransformation transformation) {
		this.transformation = transformation;
	}

	@Override
	public ITransformation getTransformation() {
		return transformation;
	}

	@Override
	public IReadonlyVector3f point() {
		return center;
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		float x2 = (x - center.getX()); x2 *= x2;
		float y2 = (y - center.getY()); y2 *= y2;
		float z2 = (z - center.getZ()); z2 *= z2;

		return x2 + y2 + z2 <= radius * radius;
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return center.getX() - radius >= minX && center.getX() + radius <= maxX
				&& center.getY() - radius >= minY && center.getY() + radius <= maxY
				&& center.getZ() - radius >= minZ && center.getZ() + radius <= maxZ;
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		float minX2 = (minX - center.getX()); minX2 *= minX2;
		float minY2 = (minY - center.getY()); minY2 *= minY2;
		float minZ2 = (minZ - center.getZ()); minZ2 *= minZ2;

		float maxX2 = (maxX - center.getX()); maxX2 *= maxX2;
		float maxY2 = (maxY - center.getY()); maxY2 *= maxY2;
		float maxZ2 = (maxZ - center.getZ()); maxZ2 *= maxZ2;

		float radius2 = radius * radius;

		if (minX2 + minY2 + minZ2 > radius2) return false;
		if (minX2 + minY2 + maxZ2 > radius2) return false;
		if (minX2 + maxY2 + minZ2 > radius2) return false;
		if (minX2 + maxY2 + maxZ2 > radius2) return false;
		if (maxX2 + minY2 + minZ2 > radius2) return false;
		if (maxX2 + minY2 + maxZ2 > radius2) return false;
		if (maxX2 + maxY2 + minZ2 > radius2) return false;
		if (maxX2 + maxY2 + maxZ2 > radius2) return false;

		return true;
	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		float halfSizeX = (maxX - minX) * 0.5f;
		float halfSizeY = (maxY - minY) * 0.5f;
		float halfSizeZ = (maxZ - minZ) * 0.5f;

		float midX = minX + halfSizeX;
		float midY = minY + halfSizeY;
		float midZ = minZ + halfSizeZ;

		float dX = Math.max(Math.abs(center.getX() - midX) - halfSizeX, 0.0f);
		float dY = Math.max(Math.abs(center.getY() - midY) - halfSizeY, 0.0f);
		float dZ = Math.max(Math.abs(center.getZ() - midZ) - halfSizeZ, 0.0f);

		return (dX * dX + dY * dY + dZ * dZ < radius * radius);
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		return Math.abs(center.getX() - xValue) <= radius;
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		return Math.abs(center.getY() - yValue) <= radius;
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		return Math.abs(center.getZ() - zValue) <= radius;
	}

	@Override
	public String toString() {
		return "sphere{center=" + center + ", radius=" + radius + "}";
	}

	@Override
	public IBounding clone() {
		return new BoundingSphere(center.clone(), radius);
	}

}
