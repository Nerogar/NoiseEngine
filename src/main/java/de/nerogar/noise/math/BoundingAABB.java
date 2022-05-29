package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.*;

public class BoundingAABB implements IBounding {

	private IVector3f position, size;
	private ITransformation transformation;

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
	public BoundingAABB(IVector3f position, IVector3f size) {
		this.position = position;
		this.size = size;
	}

	/**
	 * getter for the small corner of the AABB
	 *
	 * @return the small corner
	 */
	public IVector3f getPosition() {
		return position;
	}

	/**
	 * setter for the small corner of the AABB
	 *
	 * @param x the new x component of the small corner of the AABB
	 * @param y the new y component of the small corner of the AABB
	 * @param z the new z component of the small corner of the AABB
	 */
	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	/**
	 * setter for the small corner of the AABB
	 *
	 * @param position the small corner
	 */
	public void setPosition(IReadonlyVector3f position) {
		this.position.set(position);
	}

	/**
	 * getter for the size of the AABB
	 *
	 * @return the size
	 */
	public IVector3f getSize() {
		return size;
	}

	/**
	 * setter for the size of the AABB
	 *
	 * @param size the size
	 */
	public void setSize(IVector3f size) {
		this.size.set(size);
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
		if (transformation == null) {
			return position;
		} else {
			return position.clone().addX(transformation.getX()).addY(transformation.getY()).addZ(transformation.getZ());
		}
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		if (transformation == null) {
			return x >= position.getX() && x < position.getX() + size.getX()
					&& y >= position.getY() && y < position.getY() + size.getY()
					&& z >= position.getZ() && z < position.getZ() + size.getZ();
		} else {
			return x >= position.getX() + transformation.getX() && x < position.getX() + transformation.getX() + (size.getX() * transformation.getScaleX())
					&& y >= position.getY() + transformation.getY() && y < position.getY() + transformation.getY() + (size.getY() * transformation.getScaleY())
					&& z >= position.getZ() + transformation.getZ() && z < position.getZ() + transformation.getZ() + (size.getZ() * transformation.getScaleZ());
		}
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (transformation == null) {
			return position.getX() >= minX && position.getX() + size.getX() <= maxX
					&& position.getY() >= minY && position.getY() + size.getY() <= maxY
					&& position.getZ() >= minZ && position.getZ() + size.getZ() <= maxZ;
		} else {
			return position.getX() + transformation.getX() >= minX && position.getX() + transformation.getX() + (size.getX() * transformation.getScaleX()) <= maxX
					&& position.getY() + transformation.getY() >= minY && position.getY() + transformation.getY() + (size.getY() * transformation.getScaleY()) <= maxY
					&& position.getZ() + transformation.getZ() >= minZ && position.getZ() + transformation.getZ() + (size.getZ() * transformation.getScaleZ()) <= maxZ;
		}
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (transformation == null) {
			return minX >= position.getX() && maxX <= position.getX() + size.getX()
					&& minY >= position.getY() && maxY <= position.getY() + size.getY()
					&& minZ >= position.getZ() && maxZ <= position.getZ() + size.getZ();
		} else {
			return minX >= position.getX() + transformation.getX() && maxX <= position.getX() + transformation.getX() + (size.getX() * transformation.getScaleX())
					&& minY >= position.getY() + transformation.getY() && maxY <= position.getY() + transformation.getY() + (size.getY() * transformation.getScaleY())
					&& minZ >= position.getZ() + transformation.getZ() && maxZ <= position.getZ() + transformation.getZ() + (size.getZ() * transformation.getScaleZ());
		}
	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (transformation == null) {
			return (position.getX() < maxX && position.getX() + size.getX() > minX)
					&& (position.getY() < maxY && position.getY() + size.getY() > minY)
					&& (position.getZ() < maxZ && position.getZ() + size.getZ() > minZ);
		} else {
			return (position.getX() + transformation.getX() < maxX && position.getX() + transformation.getX() + (size.getX() * transformation.getScaleX()) > minX)
					&& (position.getY() + transformation.getY() < maxY && position.getY() + transformation.getY() + (size.getY() * transformation.getScaleY()) > minY)
					&& (position.getZ() + transformation.getZ() < maxZ && position.getZ() + transformation.getZ() + (size.getZ() * transformation.getScaleZ()) > minZ);
		}
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		if (transformation == null) {
			return position.getX() <= xValue && position.getX() + size.getX() >= xValue;
		} else {
			return position.getX() + transformation.getX() <= xValue && position.getX() + transformation.getX() + (size.getX() * transformation.getScaleX()) >= xValue;
		}
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		if (transformation == null) {
			return position.getY() <= yValue && position.getY() + size.getY() >= yValue;
		} else {
			return position.getY() + transformation.getY() <= yValue && position.getY() + transformation.getY() + (size.getY() * transformation.getScaleY()) >= yValue;
		}
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		if (transformation == null) {
			return position.getZ() <= zValue && position.getZ() + size.getZ() >= zValue;
		} else {
			return position.getZ() + transformation.getZ() <= zValue && position.getZ() + transformation.getZ() + (size.getZ() * transformation.getZ()) >= zValue;
		}
	}

	@Override
	public String toString() {
		return "AABB{pos=" + position + ", size=" + size + "}";
	}

	@Override
	public IBounding clone() {
		return new BoundingAABB(position.clone(), size.clone());
	}

}
