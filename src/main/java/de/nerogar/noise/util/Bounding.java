package de.nerogar.noise.util;

public interface Bounding extends Cloneable {

	/**
	 * returns a single point in this bounding, do not modify this point
	 *
	 * @return the point
	 */
	public Vector3f point();

	/**
	 * returns true, if the the point (x, y, z) is inside the bounding
	 *
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @param z the z coordinate of the point
	 * @return true, if the point is inside this bounding
	 */
	public boolean hasPoint(float x, float y, float z);

	/**
	 * returns true, this bounding boy is fully inside the axis aligned bounding box
	 * between (minX, minY, minZ) and (maxX, maxY, maxZ)
	 *
	 * @param minX the smallest x value of the other bounding box
	 * @param minY the smallest y value of the other bounding box
	 * @param minZ the smallest z value of the other bounding box
	 * @param maxX the biggest x value of the other bounding box
	 * @param maxY the biggest y value of the other bounding box
	 * @param maxZ the biggest z value of the other bounding box
	 * @return true, if this bounding box is fully inside the other bounding box
	 */
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	/**
	 * returns true, if the axis aligned bounding box between (minX, minY, minZ) and (maxX, maxY, maxZ)
	 * is fully inside this bounding box
	 *
	 * @param minX the smallest x value of the other bounding box
	 * @param minY the smallest y value of the other bounding box
	 * @param minZ the smallest z value of the other bounding box
	 * @param maxX the biggest x value of the other bounding box
	 * @param maxY the biggest y value of the other bounding box
	 * @param maxZ the biggest z value of the other bounding box
	 * @return true, if the other bounding box is fully inside this bounding box
	 */
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	/**
	 * returns true, if the axis aligned bounding box between (minX, minY, minZ) and (maxX, maxY, maxZ)
	 * and this bounding box overlap. That means, if there is a point (x, y, z) that is inside this bounding box
	 * and the aabb between (minX, minY, minZ) and (maxX, maxY, maxZ)
	 *
	 * @param minX the smallest x value of the other bounding box
	 * @param minY the smallest y value of the other bounding box
	 * @param minZ the smallest z value of the other bounding box
	 * @param maxX the biggest x value of the other bounding box
	 * @param maxY the biggest y value of the other bounding box
	 * @param maxZ the biggest z value of the other bounding box
	 * @return true, if this and the other bounding box overlap
	 */
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	/**
	 * returns true, if the sphere at {@code (centerX, centerY, centerZ)} and radius {@code otherRadius}
	 * and this bounding box overlap. That means, if there is a point (x, y, z) that is inside this bounding box
	 * and the sphere
	 *
	 * @param centerX     the x value of the center of the sphere
	 * @param centerY     the y value of the center of the sphere
	 * @param centerZ     the z value of the center of the sphere
	 * @param otherRadius the radius of the sphere
	 * @return true, if this and the other boundingSphere overlap
	 */
	public boolean overlaps(float centerX, float centerY, float centerZ, float otherRadius);

	/**
	 * returns true, if part of the bounding is on either eithe side of the x-plane with x = xValue
	 *
	 * @param xValue the plane position
	 * @return wether the bounding intersects the plane
	 */
	public boolean intersectsXPlane(float xValue);

	/**
	 * returns true, if part of the bounding is on either eithe side of the y-plane with y = yValue
	 *
	 * @param yValue the plane position
	 * @return wether the bounding intersects the plane
	 */
	public boolean intersectsYPlane(float yValue);

	/**
	 * returns true, if part of the bounding is on either eithe side of the z-plane with z = zValue
	 *
	 * @param zValue the plane position
	 * @return wether the bounding intersects the plane
	 */
	public boolean intersectsZPlane(float zValue);

	/**
	 * returns true, if the boundingAABB {@code bounding} and this bounding box overlap.
	 *
	 * @param bounding the other bounding
	 * @return true, if this and the other bounding box overlap
	 */
	public default boolean overlapsAABB(BoundingAABB bounding) {
		return overlaps(
				bounding.getPosition().getX(),
				bounding.getPosition().getY(),
				bounding.getPosition().getZ(),
				bounding.getPosition().getX() + bounding.getSize().getX(),
				bounding.getPosition().getY() + bounding.getSize().getY(),
				bounding.getPosition().getZ() + bounding.getSize().getZ()
		               );
	}

	/**
	 * returns true, if the boundingSphere {@code bounding} and this bounding box overlap.
	 *
	 * @param bounding the other bounding
	 * @return true, if this and the other bounding box overlap
	 */
	public default boolean overlapsSphere(BoundingSphere bounding) {
		return overlaps(bounding.getCenter().getX(), bounding.getCenter().getY(), bounding.getCenter().getZ(), bounding.getRadius());
	}

	/**
	 * returns true, if the boundingHexahedron {@code bounding} and this bounding box overlap.
	 *
	 * @param bounding the other bounding
	 * @return true, if this and the other bounding box overlap
	 */
	public boolean overlapsHexahedron(BoundingHexahedron bounding);

	/**
	 * returns true, if the other bounding {@code bounding} and this bounding box overlap.
	 *
	 * @param bounding the other bounding
	 * @return true, if this and the other bounding box overlap
	 */
	public default boolean overlapsBounding(Bounding bounding) {
		if (bounding instanceof BoundingAABB) return overlapsAABB((BoundingAABB) bounding);
		if (bounding instanceof BoundingSphere) return overlapsSphere((BoundingSphere) bounding);
		if (bounding instanceof BoundingHexahedron) return overlapsHexahedron((BoundingHexahedron) bounding);
		if (bounding instanceof BoundingPoint) {
			Vector3f position = ((BoundingPoint) bounding).getPosition();
			return hasPoint(position.getX(), position.getY(), position.getZ());
		}
		return false;
	}

	Bounding clone();

}
