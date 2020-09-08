package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IBounding;
import de.nerogar.noiseInterface.math.IReadonlyVector3f;
import de.nerogar.noiseInterface.math.IVector3f;

public class BoundingPoint implements IBounding {

	private IVector3f      position;
	private Transformation transformation;

	public BoundingPoint(IVector3f position) {
		this.position = position;
	}

	/**
	 * getter for the point
	 *
	 * @return the point
	 */
	public IVector3f getPosition() {
		return position;
	}

	/**
	 * setter for the point
	 *
	 * @param position the point
	 */
	public void setPosition(IVector3f position) {
		this.position.set(position);
	}

	@Override
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	@Override
	public Transformation getTransformation() {
		return transformation;
	}

	@Override
	public IReadonlyVector3f point() {
		return position;
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		return x == position.getX() && y == position.getY() && z == position.getZ();
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return position.getX() >= minX && position.getX() <= maxX
				&& position.getY() >= minY && position.getY() <= maxY
				&& position.getZ() >= minZ && position.getZ() <= maxZ;
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return minX >= position.getX() && maxX <= position.getX()
				&& minY >= position.getY() && maxY <= position.getY()
				&& minZ >= position.getZ() && maxZ <= position.getZ();
	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return (position.getX() < maxX && position.getX() >= minX)
				&& (position.getY() < maxY && position.getY() >= minY)
				&& (position.getZ() < maxZ && position.getZ() >= minZ);
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		return position.getX() == xValue;
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		return position.getY() == yValue;
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		return position.getZ() == zValue;
	}

	@Override
	public String toString() {
		return "point{pos=" + position + "}";
	}

	@Override
	public IBounding clone() {
		return new BoundingPoint(position.clone());
	}

}
