package de.nerogar.noise.util;

import de.nerogar.noise.exception.NotImplementedException;

public class BoundingHexahedron implements Bounding {

	private Vector3f point1;
	private Vector3f point2;
	private Vector3f point3;
	private Vector3f point4;
	private Vector3f point5;
	private Vector3f point6;
	private Vector3f point7;
	private Vector3f point8;

	private Vector3f normalA;
	private Vector3f normalB;
	private Vector3f normalC;
	private Vector3f normalD;
	private Vector3f normalE;
	private Vector3f normalF;

	private float distanceA;
	private float distanceB;
	private float distanceC;
	private float distanceD;
	private float distanceE;
	private float distanceF;

	private float minXhexahedron;
	private float maxXhexahedron;
	private float minYhexahedron;
	private float maxYhexahedron;
	private float minZhexahedron;
	private float maxZhexahedron;

	/**
	 * create a new irregular, but convex Hexahedron
	 * <p>
	 * <pre>
	 * Vertices: [1] .. [8]
	 * Faces:    (A) .. (F)
	 *
	 *         [5]-----------------------[6]
	 *         /|                        /|
	 *        / |                       / |
	 *       /  |        (C)           /  |
	 *      /   |         |           /   |
	 *     /    |         | (F)      /    |
	 *   [1]-----------------------[2]    |
	 *    |     |         |/        |     |
	 *    | (B)-|- - - - -+- - - - -|-(A) |
	 *    |     |        /|         |     |
	 *    |    [7]------/-|---------|----[8]
	 *    |    /      (E) |         |    /
	 *    |   /           |         |   /
	 *    |  /           (D)        |  /
	 *    | /                       | /
	 *    |/                        |/
	 *   [3]-----------------------[4]
	 * </pre>
	 *
	 * @param point1 a vertex of the bounding
	 * @param point2 a vertex of the bounding
	 * @param point3 a vertex of the bounding
	 * @param point4 a vertex of the bounding
	 * @param point5 a vertex of the bounding
	 * @param point6 a vertex of the bounding
	 * @param point7 a vertex of the bounding
	 * @param point8 a vertex of the bounding
	 */
	public BoundingHexahedron(Vector3f point1, Vector3f point2, Vector3f point3, Vector3f point4, Vector3f point5, Vector3f point6, Vector3f point7, Vector3f point8) {
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
		this.point4 = point4;
		this.point5 = point5;
		this.point6 = point6;
		this.point7 = point7;
		this.point8 = point8;

		setPoints(point1, point2, point3, point4, point5, point6, point7, point8);
	}

	/**
	 * update the points of this bounding according to the scheme described
	 * {@link BoundingHexahedron#BoundingHexahedron(Vector3f, Vector3f, Vector3f, Vector3f, Vector3f, Vector3f, Vector3f, Vector3f) here}
	 *
	 * @param point1 a vertex of the bounding
	 * @param point2 a vertex of the bounding
	 * @param point3 a vertex of the bounding
	 * @param point4 a vertex of the bounding
	 * @param point5 a vertex of the bounding
	 * @param point6 a vertex of the bounding
	 * @param point7 a vertex of the bounding
	 * @param point8 a vertex of the bounding
	 */
	public void setPoints(Vector3f point1, Vector3f point2, Vector3f point3, Vector3f point4, Vector3f point5, Vector3f point6, Vector3f point7, Vector3f point8) {
		this.point1.set(point1);
		this.point2.set(point2);
		this.point3.set(point3);
		this.point4.set(point4);
		this.point5.set(point5);
		this.point6.set(point6);
		this.point7.set(point7);
		this.point8.set(point8);

		normalA = point4.subtracted(point2).cross(point6.subtracted(point2)).normalize();
		normalB = point3.subtracted(point7).cross(point5.subtracted(point7)).normalize();
		normalC = point6.subtracted(point2).cross(point1.subtracted(point2)).normalize();
		normalD = point8.subtracted(point7).cross(point3.subtracted(point7)).normalize();
		normalE = point1.subtracted(point2).cross(point4.subtracted(point2)).normalize();
		normalF = point5.subtracted(point7).cross(point8.subtracted(point7)).normalize();

		distanceA = -point2.dot(normalA);
		distanceB = -point7.dot(normalB);
		distanceC = -point2.dot(normalC);
		distanceD = -point7.dot(normalD);
		distanceE = -point2.dot(normalE);
		distanceF = -point7.dot(normalF);

		minXhexahedron = maxXhexahedron = point1.getX();
		minYhexahedron = maxYhexahedron = point1.getY();
		minZhexahedron = maxZhexahedron = point1.getZ();

		minXhexahedron = Math.min(minXhexahedron, point2.getX()); maxXhexahedron = Math.max(maxXhexahedron, point2.getX());
		minYhexahedron = Math.min(minYhexahedron, point2.getY()); maxYhexahedron = Math.max(maxYhexahedron, point2.getY());
		minZhexahedron = Math.min(minZhexahedron, point2.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point2.getZ());

		minXhexahedron = Math.min(minXhexahedron, point3.getX()); maxXhexahedron = Math.max(maxXhexahedron, point3.getX());
		minYhexahedron = Math.min(minYhexahedron, point3.getY()); maxYhexahedron = Math.max(maxYhexahedron, point3.getY());
		minZhexahedron = Math.min(minZhexahedron, point3.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point3.getZ());

		minXhexahedron = Math.min(minXhexahedron, point4.getX()); maxXhexahedron = Math.max(maxXhexahedron, point4.getX());
		minYhexahedron = Math.min(minYhexahedron, point4.getY()); maxYhexahedron = Math.max(maxYhexahedron, point4.getY());
		minZhexahedron = Math.min(minZhexahedron, point4.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point4.getZ());

		minXhexahedron = Math.min(minXhexahedron, point5.getX()); maxXhexahedron = Math.max(maxXhexahedron, point5.getX());
		minYhexahedron = Math.min(minYhexahedron, point5.getY()); maxYhexahedron = Math.max(maxYhexahedron, point5.getY());
		minZhexahedron = Math.min(minZhexahedron, point5.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point5.getZ());

		minXhexahedron = Math.min(minXhexahedron, point6.getX()); maxXhexahedron = Math.max(maxXhexahedron, point6.getX());
		minYhexahedron = Math.min(minYhexahedron, point6.getY()); maxYhexahedron = Math.max(maxYhexahedron, point6.getY());
		minZhexahedron = Math.min(minZhexahedron, point6.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point6.getZ());

		minXhexahedron = Math.min(minXhexahedron, point7.getX()); maxXhexahedron = Math.max(maxXhexahedron, point7.getX());
		minYhexahedron = Math.min(minYhexahedron, point7.getY()); maxYhexahedron = Math.max(maxYhexahedron, point7.getY());
		minZhexahedron = Math.min(minZhexahedron, point7.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point7.getZ());

		minXhexahedron = Math.min(minXhexahedron, point8.getX()); maxXhexahedron = Math.max(maxXhexahedron, point8.getX());
		minYhexahedron = Math.min(minYhexahedron, point8.getY()); maxYhexahedron = Math.max(maxYhexahedron, point8.getY());
		minZhexahedron = Math.min(minZhexahedron, point8.getZ()); maxZhexahedron = Math.max(maxZhexahedron, point8.getZ());

	}

	@Override
	public Vector3f point() {
		return point1;
	}

	private boolean isInsideOfFace(Vector3f normal, float distance, float x, float y, float z) {
		return (normal.getX() * x + normal.getY() * y + normal.getZ() * z + distance) <= 0;
	}

	private float distanceToFace(Vector3f normal, float distance, float x, float y, float z) {
		return normal.getX() * x + normal.getY() * y + normal.getZ() * z + distance;
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		return (isInsideOfFace(normalA, distanceA, x, y, z))
				&& (isInsideOfFace(normalB, distanceB, x, y, z))
				&& (isInsideOfFace(normalC, distanceC, x, y, z))
				&& (isInsideOfFace(normalD, distanceD, x, y, z))
				&& (isInsideOfFace(normalE, distanceE, x, y, z))
				&& (isInsideOfFace(normalF, distanceF, x, y, z));
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return minXhexahedron >= minX && maxXhexahedron < maxX && minYhexahedron >= minY && maxYhexahedron < maxY && minZhexahedron >= minZ && maxZhexahedron < maxZ;
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {

		// hasInside is true, if each point is inside this bounding

		return hasPoint(minX, minY, minZ)
				&& hasPoint(minX, minY, maxZ)
				&& hasPoint(minX, maxY, minZ)
				&& hasPoint(minX, maxY, maxZ)
				&& hasPoint(maxX, minY, minZ)
				&& hasPoint(maxX, minY, maxZ)
				&& hasPoint(maxX, maxY, minZ)
				&& hasPoint(maxX, maxY, maxZ);

	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {

		// cheap test with aabb
		if (maxXhexahedron < minX || minXhexahedron >= maxX || maxYhexahedron < minY || minYhexahedron >= maxY || maxZhexahedron < minZ || minZhexahedron >= maxZ) return false;

		/*
		1. for every face of the AABB: test if all points are outside the face.
			if that is true for at least one face -> no overlap
		2. for every face of the hexahedron: test if test if all points of the AABB are outside the face.
			if that is true for at least one face -> no overlap
		 */

		return (point1.getX() >= minX || point2.getX() >= minX || point3.getX() >= minX || point4.getX() >= minX || point5.getX() >= minX || point6.getX() >= minX || point7.getX() >= minX || point8.getX() >= minX)
				&& (point1.getX() < maxX || point2.getX() < maxX || point3.getX() < maxX || point4.getX() < maxX || point5.getX() < maxX || point6.getX() < maxX || point7.getX() < maxX || point8.getX() < maxX)
				&& (point1.getY() >= minY || point2.getY() >= minY || point3.getY() >= minY || point4.getY() >= minY || point5.getY() >= minY || point6.getY() >= minY || point7.getY() >= minY || point8.getY() >= minY)
				&& (point1.getY() < maxY || point2.getY() < maxY || point3.getY() < maxY || point4.getY() < maxY || point5.getY() < maxY || point6.getY() < maxY || point7.getY() < maxY || point8.getY() < maxY)
				&& (point1.getZ() >= minZ || point2.getZ() >= minZ || point3.getZ() >= minZ || point4.getZ() >= minZ || point5.getZ() >= minZ || point6.getZ() >= minZ || point7.getZ() >= minZ || point8.getZ() >= minZ)
				&& (point1.getZ() < maxZ || point2.getZ() < maxZ || point3.getZ() < maxZ || point4.getZ() < maxZ || point5.getZ() < maxZ || point6.getZ() < maxZ || point7.getZ() < maxZ || point8.getZ() < maxZ)
				// A
				&& (isInsideOfFace(normalA, distanceA, minX, minY, minZ) || isInsideOfFace(normalA, distanceA, minX, minY, maxZ)
				|| isInsideOfFace(normalA, distanceA, minX, maxY, minZ) || isInsideOfFace(normalA, distanceA, minX, maxY, maxZ)
				|| isInsideOfFace(normalA, distanceA, maxX, minY, minZ) || isInsideOfFace(normalA, distanceA, maxX, minY, maxZ)
				|| isInsideOfFace(normalA, distanceA, maxX, maxY, minZ) || isInsideOfFace(normalA, distanceA, maxX, maxY, maxZ))
				// B
				&& (isInsideOfFace(normalB, distanceB, minX, minY, minZ) || isInsideOfFace(normalB, distanceB, minX, minY, maxZ)
				|| isInsideOfFace(normalB, distanceB, minX, maxY, minZ) || isInsideOfFace(normalB, distanceB, minX, maxY, maxZ)
				|| isInsideOfFace(normalB, distanceB, maxX, minY, minZ) || isInsideOfFace(normalB, distanceB, maxX, minY, maxZ)
				|| isInsideOfFace(normalB, distanceB, maxX, maxY, minZ) || isInsideOfFace(normalB, distanceB, maxX, maxY, maxZ))
				// C
				&& (isInsideOfFace(normalC, distanceC, minX, minY, minZ) || isInsideOfFace(normalC, distanceC, minX, minY, maxZ)
				|| isInsideOfFace(normalC, distanceC, minX, maxY, minZ) || isInsideOfFace(normalC, distanceC, minX, maxY, maxZ)
				|| isInsideOfFace(normalC, distanceC, maxX, minY, minZ) || isInsideOfFace(normalC, distanceC, maxX, minY, maxZ)
				|| isInsideOfFace(normalC, distanceC, maxX, maxY, minZ) || isInsideOfFace(normalC, distanceC, maxX, maxY, maxZ))
				// D
				&& (isInsideOfFace(normalD, distanceD, minX, minY, minZ) || isInsideOfFace(normalD, distanceD, minX, minY, maxZ)
				|| isInsideOfFace(normalD, distanceD, minX, maxY, minZ) || isInsideOfFace(normalD, distanceD, minX, maxY, maxZ)
				|| isInsideOfFace(normalD, distanceD, maxX, minY, minZ) || isInsideOfFace(normalD, distanceD, maxX, minY, maxZ)
				|| isInsideOfFace(normalD, distanceD, maxX, maxY, minZ) || isInsideOfFace(normalD, distanceD, maxX, maxY, maxZ))
				// E
				&& (isInsideOfFace(normalE, distanceE, minX, minY, minZ) || isInsideOfFace(normalE, distanceE, minX, minY, maxZ)
				|| isInsideOfFace(normalE, distanceE, minX, maxY, minZ) || isInsideOfFace(normalE, distanceE, minX, maxY, maxZ)
				|| isInsideOfFace(normalE, distanceE, maxX, minY, minZ) || isInsideOfFace(normalE, distanceE, maxX, minY, maxZ)
				|| isInsideOfFace(normalE, distanceE, maxX, maxY, minZ) || isInsideOfFace(normalE, distanceE, maxX, maxY, maxZ))
				// F
				&& (isInsideOfFace(normalF, distanceF, minX, minY, minZ) || isInsideOfFace(normalF, distanceF, minX, minY, maxZ)
				|| isInsideOfFace(normalF, distanceF, minX, maxY, minZ) || isInsideOfFace(normalF, distanceF, minX, maxY, maxZ)
				|| isInsideOfFace(normalF, distanceF, maxX, minY, minZ) || isInsideOfFace(normalF, distanceF, maxX, minY, maxZ)
				|| isInsideOfFace(normalF, distanceF, maxX, maxY, minZ) || isInsideOfFace(normalF, distanceF, maxX, maxY, maxZ));

	}

	@Override
	public boolean overlaps(float centerX, float centerY, float centerZ, float otherRadius) {
		throw new NotImplementedException(); // TODO implement overlapping test
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		return minXhexahedron <= xValue && maxXhexahedron >= xValue;
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		return minYhexahedron <= yValue && maxYhexahedron >= yValue;
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		return minZhexahedron <= zValue && maxZhexahedron >= zValue;
	}

	@Override
	public boolean overlapsHexahedron(BoundingHexahedron bounding) {

		// cheap test with aabb
		if (maxXhexahedron < bounding.minXhexahedron || minXhexahedron >= bounding.maxXhexahedron
				|| maxYhexahedron < bounding.minYhexahedron || minYhexahedron >= bounding.maxYhexahedron
				|| maxZhexahedron < bounding.minZhexahedron || minZhexahedron >= bounding.maxZhexahedron) return false;

		// works the same way as the overlaps method

		return (isInsideOfFace(bounding.normalA, bounding.distanceA, point1.getX(), point1.getX(), point1.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point2.getX(), point2.getY(), point2.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point3.getX(), point3.getY(), point3.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point4.getX(), point4.getY(), point4.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point5.getX(), point5.getY(), point5.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point6.getX(), point6.getY(), point6.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point7.getX(), point7.getY(), point7.getZ())
				|| isInsideOfFace(bounding.normalA, bounding.distanceA, point8.getX(), point8.getY(), point8.getZ()))
				// B other
				&& (isInsideOfFace(bounding.normalB, bounding.distanceB, point1.getX(), point1.getY(), point1.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point2.getX(), point2.getY(), point2.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point3.getX(), point3.getY(), point3.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point4.getX(), point4.getY(), point4.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point5.getX(), point5.getY(), point5.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point6.getX(), point6.getY(), point6.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point7.getX(), point7.getY(), point7.getZ())
				|| isInsideOfFace(bounding.normalB, bounding.distanceB, point8.getX(), point8.getY(), point8.getZ()))
				// C other
				&& (isInsideOfFace(bounding.normalC, bounding.distanceC, point1.getX(), point1.getY(), point1.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point2.getX(), point2.getY(), point2.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point3.getX(), point3.getY(), point3.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point4.getX(), point4.getY(), point4.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point5.getX(), point5.getY(), point5.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point6.getX(), point6.getY(), point6.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point7.getX(), point7.getY(), point7.getZ())
				|| isInsideOfFace(bounding.normalC, bounding.distanceC, point8.getX(), point8.getY(), point8.getZ()))
				// D other
				&& (isInsideOfFace(bounding.normalD, bounding.distanceD, point1.getX(), point1.getY(), point1.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point2.getX(), point2.getY(), point2.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point3.getX(), point3.getY(), point3.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point4.getX(), point4.getY(), point4.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point5.getX(), point5.getY(), point5.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point6.getX(), point6.getY(), point6.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point7.getX(), point7.getY(), point7.getZ())
				|| isInsideOfFace(bounding.normalD, bounding.distanceD, point8.getX(), point8.getY(), point8.getZ()))
				// E other
				&& (isInsideOfFace(bounding.normalE, bounding.distanceE, point1.getX(), point1.getY(), point1.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point2.getX(), point2.getY(), point2.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point3.getX(), point3.getY(), point3.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point4.getX(), point4.getY(), point4.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point5.getX(), point5.getY(), point5.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point6.getX(), point6.getY(), point6.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point7.getX(), point7.getY(), point7.getZ())
				|| isInsideOfFace(bounding.normalE, bounding.distanceE, point8.getX(), point8.getY(), point8.getZ()))
				// F other
				&& (isInsideOfFace(bounding.normalF, bounding.distanceF, point1.getX(), point1.getY(), point1.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point2.getX(), point2.getY(), point2.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point3.getX(), point3.getY(), point3.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point4.getX(), point4.getY(), point4.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point5.getX(), point5.getY(), point5.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point6.getX(), point6.getY(), point6.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point7.getX(), point7.getY(), point7.getZ())
				|| isInsideOfFace(bounding.normalF, bounding.distanceF, point8.getX(), point8.getY(), point8.getZ()))
				// A self
				&& (isInsideOfFace(normalA, distanceA, bounding.point1.getX(), bounding.point1.getY(), bounding.point1.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point2.getX(), bounding.point2.getY(), bounding.point2.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point3.getX(), bounding.point3.getY(), bounding.point3.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point4.getX(), bounding.point4.getY(), bounding.point4.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point5.getX(), bounding.point5.getY(), bounding.point5.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point6.getX(), bounding.point6.getY(), bounding.point6.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point7.getX(), bounding.point7.getY(), bounding.point7.getZ())
				|| isInsideOfFace(normalA, distanceA, bounding.point8.getX(), bounding.point8.getY(), bounding.point8.getZ()))
				// B self
				&& (isInsideOfFace(normalB, distanceB, bounding.point1.getX(), bounding.point1.getY(), bounding.point1.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point2.getX(), bounding.point2.getY(), bounding.point2.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point3.getX(), bounding.point3.getY(), bounding.point3.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point4.getX(), bounding.point4.getY(), bounding.point4.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point5.getX(), bounding.point5.getY(), bounding.point5.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point6.getX(), bounding.point6.getY(), bounding.point6.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point7.getX(), bounding.point7.getY(), bounding.point7.getZ())
				|| isInsideOfFace(normalB, distanceB, bounding.point8.getX(), bounding.point8.getY(), bounding.point8.getZ()))
				// C self
				&& (isInsideOfFace(normalC, distanceC, bounding.point1.getX(), bounding.point1.getY(), bounding.point1.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point2.getX(), bounding.point2.getY(), bounding.point2.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point3.getX(), bounding.point3.getY(), bounding.point3.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point4.getX(), bounding.point4.getY(), bounding.point4.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point5.getX(), bounding.point5.getY(), bounding.point5.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point6.getX(), bounding.point6.getY(), bounding.point6.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point7.getX(), bounding.point7.getY(), bounding.point7.getZ())
				|| isInsideOfFace(normalC, distanceC, bounding.point8.getX(), bounding.point8.getY(), bounding.point8.getZ()))
				// D self
				&& (isInsideOfFace(normalD, distanceD, bounding.point1.getX(), bounding.point1.getY(), bounding.point1.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point2.getX(), bounding.point2.getY(), bounding.point2.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point3.getX(), bounding.point3.getY(), bounding.point3.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point4.getX(), bounding.point4.getY(), bounding.point4.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point5.getX(), bounding.point5.getY(), bounding.point5.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point6.getX(), bounding.point6.getY(), bounding.point6.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point7.getX(), bounding.point7.getY(), bounding.point7.getZ())
				|| isInsideOfFace(normalD, distanceD, bounding.point8.getX(), bounding.point8.getY(), bounding.point8.getZ()))
				// E self
				&& (isInsideOfFace(normalE, distanceE, bounding.point1.getX(), bounding.point1.getY(), bounding.point1.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point2.getX(), bounding.point2.getY(), bounding.point2.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point3.getX(), bounding.point3.getY(), bounding.point3.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point4.getX(), bounding.point4.getY(), bounding.point4.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point5.getX(), bounding.point5.getY(), bounding.point5.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point6.getX(), bounding.point6.getY(), bounding.point6.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point7.getX(), bounding.point7.getY(), bounding.point7.getZ())
				|| isInsideOfFace(normalE, distanceE, bounding.point8.getX(), bounding.point8.getY(), bounding.point8.getZ()))
				// F self
				&& (isInsideOfFace(normalF, distanceF, bounding.point1.getX(), bounding.point1.getY(), bounding.point1.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point2.getX(), bounding.point2.getY(), bounding.point2.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point3.getX(), bounding.point3.getY(), bounding.point3.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point4.getX(), bounding.point4.getY(), bounding.point4.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point5.getX(), bounding.point5.getY(), bounding.point5.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point6.getX(), bounding.point6.getY(), bounding.point6.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point7.getX(), bounding.point7.getY(), bounding.point7.getZ())
				|| isInsideOfFace(normalF, distanceF, bounding.point8.getX(), bounding.point8.getY(), bounding.point8.getZ()));
	}

	@Override
	public Bounding clone() {
		return new BoundingHexahedron(point1, point2, point3, point4, point5, point6, point7, point8);
	}

}
