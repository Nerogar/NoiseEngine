package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IBounding;
import de.nerogar.noiseInterface.math.IReadonlyVector3f;
import de.nerogar.noiseInterface.math.IVector3f;

public class BoundingAABB implements IBounding {

	private IVector3f position, size;
	private Transformation transformation;

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
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	@Override
	public Transformation getTransformation() {
		return transformation;
	}

	@Override
	public IReadonlyVector3f point() {
		if (transformation == null) {
			return position;
		} else {
			return position.clone().addX(transformation.x).addY(transformation.y).addZ(transformation.z);
		}
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		if (transformation == null) {
			return x >= position.getX() && x < position.getX() + size.getX()
					&& y >= position.getY() && y < position.getY() + size.getY()
					&& z >= position.getZ() && z < position.getZ() + size.getZ();
		} else {
			return x >= position.getX() + transformation.x && x < position.getX() + transformation.x + (size.getX() * transformation.scaleX)
					&& y >= position.getY() + transformation.y && y < position.getY() + transformation.y + (size.getY() * transformation.scaleY)
					&& z >= position.getZ() + transformation.z && z < position.getZ() + transformation.z + (size.getZ() * transformation.scaleZ);
		}
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (transformation == null) {
			return position.getX() >= minX && position.getX() + size.getX() <= maxX
					&& position.getY() >= minY && position.getY() + size.getY() <= maxY
					&& position.getZ() >= minZ && position.getZ() + size.getZ() <= maxZ;
		} else {
			return position.getX() + transformation.x >= minX && position.getX() + transformation.x + (size.getX() * transformation.scaleX) <= maxX
					&& position.getY() + transformation.y >= minY && position.getY() + transformation.y + (size.getY() * transformation.scaleY) <= maxY
					&& position.getZ() + transformation.z >= minZ && position.getZ() + transformation.z + (size.getZ() * transformation.scaleZ) <= maxZ;
		}
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (transformation == null) {
			return minX >= position.getX() && maxX <= position.getX() + size.getX()
					&& minY >= position.getY() && maxY <= position.getY() + size.getY()
					&& minZ >= position.getZ() && maxZ <= position.getZ() + size.getZ();
		} else {
			return minX >= position.getX() + transformation.x && maxX <= position.getX() + transformation.x + (size.getX() * transformation.scaleX)
					&& minY >= position.getY() + transformation.y && maxY <= position.getY() + transformation.y + (size.getY() * transformation.scaleY)
					&& minZ >= position.getZ() + transformation.z && maxZ <= position.getZ() + transformation.z + (size.getZ() * transformation.scaleZ);
		}
	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (transformation == null) {
			return (position.getX() < maxX && position.getX() + size.getX() > minX)
					&& (position.getY() < maxY && position.getY() + size.getY() > minY)
					&& (position.getZ() < maxZ && position.getZ() + size.getZ() > minZ);
		} else {
			return (position.getX() + transformation.x < maxX && position.getX() + transformation.x + (size.getX() * transformation.scaleX) > minX)
					&& (position.getY() + transformation.y < maxY && position.getY() + transformation.y + (size.getY() * transformation.scaleY) > minY)
					&& (position.getZ() + transformation.z < maxZ && position.getZ() + transformation.z + (size.getZ() * transformation.scaleZ) > minZ);
		}
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		if (transformation == null) {
			return position.getX() <= xValue && position.getX() + size.getX() >= xValue;
		} else {
			return position.getX() + transformation.x <= xValue && position.getX() + transformation.x + (size.getX() * transformation.scaleX) >= xValue;
		}
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		if (transformation == null) {
			return position.getY() <= yValue && position.getY() + size.getY() >= yValue;
		} else {
			return position.getY() + transformation.y <= yValue && position.getY() + transformation.y + (size.getY() * transformation.scaleY) >= yValue;
		}
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		if (transformation == null) {
			return position.getZ() <= zValue && position.getZ() + size.getZ() >= zValue;
		} else {
			return position.getZ() + transformation.z <= zValue && position.getZ() + transformation.z + (size.getZ() * transformation.z) >= zValue;
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
