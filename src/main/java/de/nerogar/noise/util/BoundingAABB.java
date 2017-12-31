package de.nerogar.noise.util;

public class BoundingAABB implements Bounding {

	private Vector3f position, size;

	/**
	 * create an axis aligned bounding box, saved as two vectors,
	 * the small corner and the size.
	 * The small corner is included, the big corner is not included.
	 * That means, that two AABBs with parameters:
	 * <ol>
	 * <li>position = (0, 0, 0) size = (1, 1, 1)</li>
	 * <li>position = (1, 0, 0) size = (1, 1, 1)</li>
	 * </ol>
	 * do not have an intersection.
	 *
	 * @param position the small corner of the AABB
	 * @param size     teh size of the AABB
	 */
	public BoundingAABB(Vector3f position, Vector3f size) {
		this.position = position;
		this.size = size;
	}

	/**
	 * getter for the small corner of the AABB
	 *
	 * @return the small corner
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * setter for the small corner of the AABB
	 *
	 * @param x the new x component of the center of the sphere
	 * @param y the new y component of the center of the sphere
	 * @param z the new z component of the center of the sphere
	 */
	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	/**
	 * getter for the size of the AABB
	 *
	 * @return the size
	 */
	public Vector3f getSize() {
		return size;
	}

	/**
	 * setter for the size of the AABB
	 *
	 * @param size the size
	 */
	public void setSize(Vector3f size) {
		this.size.set(size);
	}

	@Override
	public Vector3f point() {
		return position;
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		return x >= position.getX() && x < position.getX() + size.getX()
				&& y >= position.getY() && y < position.getY() + size.getY()
				&& z >= position.getZ() && z < position.getZ() + size.getZ();
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return position.getX() >= minX && position.getX() + size.getX() <= maxX
				&& position.getY() >= minY && position.getY() + size.getY() <= maxY
				&& position.getZ() >= minZ && position.getZ() + size.getZ() <= maxZ;
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return minX >= position.getX() && maxX <= position.getX() + size.getX()
				&& minY >= position.getY() && maxY <= position.getY() + size.getY()
				&& minZ >= position.getZ() && maxZ <= position.getZ() + size.getZ();
	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return (position.getX() < maxX && position.getX() + size.getX() > minX)
				&& (position.getY() < maxY && position.getY() + size.getY() > minY)
				&& (position.getZ() < maxZ && position.getZ() + size.getZ() > minZ);
	}

	@Override
	public boolean overlaps(float centerX, float centerY, float centerZ, float otherRadius) {
		float halfSizeX = (size.getX()) * 0.5f;
		float halfSizeY = (size.getY()) * 0.5f;
		float halfSizeZ = (size.getZ()) * 0.5f;

		float midX = position.getX() + halfSizeX;
		float midY = position.getY() + halfSizeY;
		float midZ = position.getZ() + halfSizeZ;

		float dX = Math.max(Math.abs(centerX - midX) - halfSizeX, 0.0f);
		float dY = Math.max(Math.abs(centerY - midY) - halfSizeY, 0.0f);
		float dZ = Math.max(Math.abs(centerZ - midZ) - halfSizeZ, 0.0f);

		return (dX * dX + dY * dY + dZ * dZ < otherRadius * otherRadius);
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		return position.getX() <= xValue && position.getX() + size.getX() >= xValue;
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		return position.getY() <= yValue && position.getY() + size.getY() >= yValue;
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		return position.getZ() <= zValue && position.getZ() + size.getZ() >= zValue;
	}

	@Override
	public boolean overlapsHexahedron(BoundingHexahedron bounding) {
		return bounding.overlapsAABB(this);
	}

	@Override
	public String toString() {
		return "AABB{pos=" + position + ", size=" + size + "}";
	}

	@Override
	public Bounding clone() {
		return new BoundingAABB(position.clone(), size.clone());
	}

}
