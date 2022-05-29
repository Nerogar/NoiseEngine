package de.nerogar.noiseInterface.math;

public interface IBounding extends Cloneable {

	void setTransformation(ITransformation transformation);

	ITransformation getTransformation();

	/**
	 * returns a single point in this bounding
	 *
	 * @return the point
	 */
	IReadonlyVector3f point();

	/**
	 * returns true, if the the point (x, y, z) is inside this bounding
	 *
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @param z the z coordinate of the point
	 * @return true, if the point is inside this bounding
	 */
	boolean hasPoint(float x, float y, float z);

	/**
	 * returns true, if this bounding is fully inside the axis aligned bounding box
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
	boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	/**
	 * returns true, if the axis aligned bounding box between (minX, minY, minZ) and (maxX, maxY, maxZ)
	 * is fully inside this bounding
	 *
	 * @param minX the smallest x value of the other bounding box
	 * @param minY the smallest y value of the other bounding box
	 * @param minZ the smallest z value of the other bounding box
	 * @param maxX the biggest x value of the other bounding box
	 * @param maxY the biggest y value of the other bounding box
	 * @param maxZ the biggest z value of the other bounding box
	 * @return true, if the other bounding box is fully inside this bounding box
	 */
	boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

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
	boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

	/**
	 * returns true, if part of the bounding is on either either side of the x-plane with x = xValue
	 *
	 * @param xValue the plane position
	 * @return whether the bounding intersects the plane
	 */
	boolean intersectsXPlane(float xValue);

	/**
	 * returns true, if part of the bounding is on either either side of the y-plane with y = yValue
	 *
	 * @param yValue the plane position
	 * @return whether the bounding intersects the plane
	 */
	boolean intersectsYPlane(float yValue);

	/**
	 * returns true, if part of the bounding is on either either side of the z-plane with z = zValue
	 *
	 * @param zValue the plane position
	 * @return whether the bounding intersects the plane
	 */
	boolean intersectsZPlane(float zValue);

	IBounding clone();

}
