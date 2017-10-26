package de.nerogar.noise.util;

public class BoundingPoint implements Bounding {

	private Vector3f position;

	public BoundingPoint(Vector3f position) {
		this.position = position;
	}

	/**
	 * getter for the point
	 *
	 * @return the point
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * setter for the point
	 *
	 * @param position the point
	 */
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	@Override
	public Vector3f point() {
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
	public boolean overlaps(float centerX, float centerY, float centerZ, float otherRadius) {
		float xd = position.getX() - centerX;
		float yd = position.getY() - centerY;
		float zd = position.getZ() - centerZ;

		return (xd * xd + yd * yd + zd * zd) < (otherRadius * otherRadius);
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
	public boolean overlapsHexahedron(BoundingHexahedron bounding) {
		return bounding.hasPoint(position.getX(), position.getY(), position.getZ());
	}

	@Override
	public boolean overlapsBounding(Bounding bounding) {
		return bounding.hasPoint(position.getX(), position.getY(), position.getZ());
	}

	@Override
	public Bounding clone() {
		return new BoundingPoint(position.clone());
	}

}
