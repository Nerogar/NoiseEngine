package de.nerogar.noise.math;

import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.*;

public class MathHelper {

	public static final float PI = (float) Math.PI;

	private static final float EPSILON = 0.000001f;

	public static float clamp(float val, float min, float max) {
		return Math.min(Math.max(min, val), max);
	}

	public static int clamp(int val, int min, int max) {
		return Math.min(Math.max(min, val), max);
	}

	/**
	 * Returns the fractional part of a float.
	 * The fractional part is positive for positive numbers and negative for negative numbers.
	 *
	 * @param val the value
	 * @return the fractional part
	 */
	public static float fract(float val) {
		return val - ((int) val);
	}

	/**
	 * Mixes the values x and y with the factor a. If a is 0 x is returned. If a is 1 y is returned.
	 *
	 * @param x the first value
	 * @param y the second value
	 * @param a the mix factor
	 * @return a mix between x and y
	 */
	public static float mix(float x, float y, float a) {
		return x * (1.0f - a) + y * a;
	}

	public static float cot(float x) {
		return 1f / (float) Math.tan(x);
	}

	public static float acot(float x) {
		return PI / 2 - (float) Math.atan(x);
	}

	/**
	 * only works if the ray direction is normalized
	 *
	 * @return the distance between a ray and a point
	 */
	public static float rayPointDistance(Ray ray, Vector3f point) {
		float pointDistX = point.getX() - ray.getStart().getX();
		float pointDistY = point.getY() - ray.getStart().getY();
		float pointDistZ = point.getZ() - ray.getStart().getZ();

		float t0 = ray.getDir().dot(new Vector3f(pointDistX, pointDistY, pointDistZ));
		if (t0 < 0) {
			return point.subtracted(ray.getStart()).getLength();
		} else {
			float dx = point.getX() - (ray.getStart().getX() + t0 * ray.getDir().getX());
			float dy = point.getY() - (ray.getStart().getY() + t0 * ray.getDir().getY());
			float dz = point.getZ() - (ray.getStart().getZ() + t0 * ray.getDir().getZ());

			return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		}
	}

	/**
	 * Calculates the closest point on the line segment between {@code vert0} and {@code vert1} to the given point
	 *
	 * @return the closes point on the ray
	 */
	public static IVector3f closestPointOnLineSegment(IReadonlyVector3f vert0, IReadonlyVector3f vert1, IReadonlyVector3f point) {
		float lineDirX = vert1.getX() - vert0.getX();
		float lineDirY = vert1.getY() - vert0.getY();
		float lineDirZ = vert1.getZ() - vert0.getZ();

		float d = (float) Math.sqrt(lineDirX * lineDirX + lineDirY * lineDirY + lineDirZ * lineDirZ);

		float pointDistX = point.getX() - vert0.getX();
		float pointDistY = point.getY() - vert0.getY();
		float pointDistZ = point.getZ() - vert0.getZ();

		float t0 = (lineDirX / d) * pointDistX + (lineDirY / d) * pointDistY + (lineDirZ / d) * pointDistZ;
		if (t0 < 0) {
			return vert0.clone();
		} else if (t0 > d) {
			return vert1.clone();
		} else {
			return new Vector3f(
					vert0.getX() + (t0 * lineDirX / d),
					vert0.getY() + (t0 * lineDirY / d),
					vert0.getZ() + (t0 * lineDirZ / d)
			);
		}
	}

	/**
	 * Calculates the closest point on a circle to a point.
	 *
	 * @param center the center of the circle
	 * @param normal the normal direction of the circle
	 * @param radius the radius of the circle
	 * @param point  the point
	 * @return the closes point on the circle
	 */
	public static IVector3f closestPointOnCircle(IReadonlyVector3f center, IReadonlyVector3f normal, float radius, IReadonlyVector3f point) {
		float pointDistX = point.getX() - center.getX();
		float pointDistY = point.getY() - center.getY();
		float pointDistZ = point.getZ() - center.getZ();

		float dot = pointDistX * normal.getX() + pointDistY * normal.getY() + pointDistZ * normal.getZ();

		float projectedX = pointDistX - dot * normal.getX();
		float projectedY = pointDistY - dot * normal.getY();
		float projectedZ = pointDistZ - dot * normal.getZ();

		float d = (float) Math.sqrt(projectedX * projectedX + projectedY * projectedY + projectedZ * projectedZ);

		projectedX /= d;
		projectedY /= d;
		projectedZ /= d;

		projectedX *= radius;
		projectedY *= radius;
		projectedZ *= radius;

		return new Vector3f(
				center.getX() + projectedX,
				center.getY() + projectedY,
				center.getZ() + projectedZ
		);
	}

	/**
	 * Calculates the closest point on a circle segment to a point.
	 * The circle segment is the shorter distance between the two vectors {@code v0} and {@code v1}
	 *
	 * @param center the center of the circle
	 * @param v0     the first enclosing vector of the circle segment
	 * @param v1     the second enclosing vector of the circle segment
	 * @param radius the radius of the circle
	 * @param point  the point
	 * @return the closes point on the circle
	 */
	public static IVector3f closestPointOnCircleSegment(IReadonlyVector3f center, IReadonlyVector3f v0, IReadonlyVector3f v1, float radius, IReadonlyVector3f point) {
		IVector3f normal = v0.crossed(v1).normalize();
		IVector3f closestPoint = closestPointOnCircle(center, normal, radius, point);

		if (isVectorBetween(closestPoint, v0, v1)) {
			return closestPoint;
		} else {
			float d0 = closestPoint.set(v0).add(center).setLength(radius).subtract(point).getLength();
			float d1 = closestPoint.set(v1).add(center).setLength(radius).subtract(point).getLength();

			if (d0 < d1) {
				return closestPoint.set(v0).add(center).setLength(radius);
			} else {
				return closestPoint.set(v1).add(center).setLength(radius);
			}
		}
	}

	/**
	 * Calculates the intersection position of a ray and a circle.
	 *
	 * @param x0 the x coordinate of the ray origin
	 * @param y0 the y coordinate of the ray origin
	 * @param dx the x coordinate of the ray direction
	 * @param dy the y coordinate of the ray direction
	 * @param cx the x coordinate of the circle center
	 * @param cy the y coordinate of the circle center
	 * @param r  the radius of the circle
	 * @return the distance on the ray for the intersection point or negative infinity in case of no intersection
	 */
	public static float rayCircleIntersection(float x0, float y0, float dx, float dy, float cx, float cy, float r) {
		// X: point in space, X0: ray origin, D: ray direction, t: position on the ray, r: radius of the circle
		//
		// ray: X = X0 + tD
		// circle: |X| = r (a circle centered around the origin)
		//
		// expanding |X0 + tD| = r gives
		// (x0^2 + y0^2) + (2*x0*dx + 2*y0*dy)*t + (dx^2 + dy^2)*t^2 = r^2
		// or
		// <D, D> * t^2 + 2<X0, D> * t + <X0, X0> - r^2 = 0 (where <_,_> is the scalar product
		// solving for t:
		// p = 2<X0, D>/<D, D>
		// q = (<X0, X0> - r^2)/<D, D>
		// t = -(p/2) +- sqrt((p/2)^2) - 2q)
		//
		// t = -(<X0, D>/<D, D>) +- sqrt((<X0, D>/<D, D>)^2 - (<X0, X0> - r^2)/<D, D>)

		x0 -= cx;
		y0 -= cy;

		// d = <D, D>
		float d = dx * dx + dy * dy;

		float p = 2 * (x0 * dx + y0 * dy) / d;
		float q = ((x0 * x0 + y0 * y0) - r * r) / d;

		// discriminant = (<X0, D>/<D, D>)^2 - (<X0, X0> - r^2)/<D, D>
		float discriminant = ((p / 2) * (p / 2)) - q;

		// if discriminant is smaller that 0 no intersection exists
		if (discriminant < 0) {
			return -1;
		}

		// not interested in the + case, because it will always be greater and therefore farther away
		float intersection = (-(p / 2) - (float) Math.sqrt(discriminant)) * (float) Math.sqrt(d);

		if (intersection >= 0) {
			return intersection;
		} else if (q < 0) {
			return intersection;
		} else {
			return Float.NEGATIVE_INFINITY;
		}
	}

	/**
	 * A version of {@link MathHelper#rayCircleIntersection(float, float, float, float, float, float, float)}
	 * where the direction vector is assumed to be normalized.
	 *
	 * @param x0 the x coordinate of the ray origin
	 * @param y0 the y coordinate of the ray origin
	 * @param dx the x coordinate of the ray direction
	 * @param dy the y coordinate of the ray direction
	 * @param cx the x coordinate of the circle center
	 * @param cy the y coordinate of the circle center
	 * @param r  the radius of the circle
	 * @return the distance on the ray for the intersection point or negative infinity in case of no intersection
	 */
	public static float rayCircleIntersectionNormalized(float x0, float y0, float dx, float dy, float cx, float cy, float r) {
		x0 -= cx;
		y0 -= cy;

		float p = 2 * (x0 * dx + y0 * dy);
		float q = ((x0 * x0 + y0 * y0) - r * r);

		// discriminant = (<X0, D>/<D, D>)^2 - (<X0, X0> - r^2)/<D, D>
		float discriminant = ((p / 2) * (p / 2)) - q;

		// if discriminant is smaller that 0 no intersection exists
		if (discriminant < 0) {
			return Float.NEGATIVE_INFINITY;
		}

		// not interested in the + case, because it will always be greater and therefore farther away
		float intersection = (-(p / 2) - (float) Math.sqrt(discriminant));

		if (intersection >= 0) {
			return intersection;
		} else if (q < 0) {
			return intersection;
		} else {
			return Float.NEGATIVE_INFINITY;
		}
	}

	/**
	 * Calculates the intersection position of a ray with a line segment.
	 * This variant is culling, meaning that the ray only collides with one side (the right side) of the line segment.
	 *
	 * @param x0     the x coordinate of the ray origin
	 * @param y0     the y coordinate of the ray origin
	 * @param dx     the x coordinate of the ray direction
	 * @param dy     the y coordinate of the ray direction
	 * @param lineX0 the x coordinate of the start position of the line segment
	 * @param lineY0 the y coordinate of the start position of the line segment
	 * @param lineX1 the x coordinate of the end position of the line segment
	 * @param lineY1 the y coordinate of the end position of the line segment
	 * @return the intersection position on the ray or a negative value in case of no intersection
	 */
	public static float rayLineSegmentIntersectionCulling(float x0, float y0, float dx, float dy, float lineX0, float lineY0, float lineX1, float lineY1) {
		// X: point in space, X0: ray origin, D1: ray direction, t1: position on the ray, D2: end of the line segment, t2: position on the line segment
		//
		// ray: X = X0 + t1*D1
		// line segment: X = t2*D2 (line segment starting at the origin)
		//
		// X0 + t1*D1 = t2*D2

		float d2x = lineX1 - lineX1;
		float d2y = lineY1 - lineY1;

		// culling (where the normal is nx = d2y, ny = -d2x)
		if ((d2y * dx - d2x * dy) <= 0) {
			return -1;
		}

		// TODO: implement

		//float t2 =

		/*if () {
			return -1;
		}*/

		return 0;
	}

	/**
	 * Möller-Trumbore intersection test
	 * taken from: "Fast, Minimum Storage Ray/Triangle Intersection" (MollerTrumbore97)
	 *
	 * @return the intersection point or null for no intersection
	 */
	public static Float rayTriangleIntersectionCulling(Vector3f orig, Vector3f dir, Vector3f vert0, Vector3f vert1, Vector3f vert2) {

		// find vectors for two edges sharing vert0
		Vector3f edge1 = vert1.clone().subtract(vert0);
		Vector3f edge2 = vert2.clone().subtract(vert0);

		// begin calculating determinant - also used to calculate U parameter
		Vector3f pVec = dir.clone().cross(edge2);

		// if determinant is near zero, ray lies in plane of triangle
		float det = edge1.dot(pVec);

		// TEST_CULL = true variant

		if (det < EPSILON) return null;

		// calculate distance from vert0 to ray origin
		Vector3f tVec = orig.clone().subtract(vert0);

		// calculate U parameter and test bounds
		float u = tVec.dot(pVec);
		if (u < 0.0f || u > det) return null;

		// prepare to test V parameter
		Vector3f qVec = tVec.clone().cross(edge1);

		// calculate V parameter and test bounds
		float v = dir.dot(qVec);
		if (v < 0.0f || u + v > det) return null;

		// calculate t, scale parameters, ray intersects triangle
		float t = edge2.dot(qVec);
		t /= det; // in the paper t is multiplied by the inverse of det. that is not necessary here because we only modify t, not u and v

		// return intersection point
		if (t > 0) {
			return t;
		} else {
			return null;
		}
	}

	/**
	 * Möller-Trumbore intersection test
	 * taken from: "Fast, Minimum Storage Ray/Triangle Intersection" (MollerTrumbore97)
	 *
	 * @return the intersection point or null for no intersection
	 */
	public static Float rayTriangleIntersectionNonCulling(Vector3f orig, Vector3f dir, Vector3f vert0, Vector3f vert1, Vector3f vert2) {

		// find vectors for two edges sharing vert0
		Vector3f edge1 = vert1.clone().subtract(vert0);
		Vector3f edge2 = vert2.clone().subtract(vert0);

		// begin calculating determinant - also used to calculate U parameter
		Vector3f pVec = dir.clone().cross(edge2);

		// if determinant is near zero, ray lies in plane of triangle
		float det = edge1.dot(pVec);

		// TEST_CULL = false variant

		if (det > -EPSILON && det < EPSILON) return null;
		float invDet = 1.0f / det;

		// calculate distance from vert0 to ray origin
		Vector3f tVec = orig.clone().subtract(vert0);

		// calculate U parameter and test bounds
		float u = tVec.dot(pVec) * invDet;
		if (u < 0.0f || u > 1.0f) return null;

		// prepare to test V parameter
		Vector3f qVec = tVec.clone().cross(edge1);

		// calculate V parameter and test bounds
		float v = dir.dot(qVec) * invDet;
		if (v < 0.0f || u + v > 1.0f) return null;

		// calculate t, ray intersects triangle
		float t = edge2.dot(qVec) * invDet;

		// return intersection point
		if (t > 0) {
			return t;
		} else {
			return null;
		}
	}

	/**
	 * Möller-Trumbore intersection test
	 * taken from: "Fast, Minimum Storage Ray/Triangle Intersection" (MollerTrumbore97)
	 *
	 * @return the intersection point or null for no intersection
	 */
	public static Float rayTriangleIntersectionCulling(
			float oX, float oY, float oZ,
			float dX, float dY, float dZ,
			float v0X, float v0Y, float v0Z,
			float v1X, float v1Y, float v1Z,
			float v2X, float v2Y, float v2Z
	                                                  ) {

		// find vectors for two edges sharing vert0
		float e1X = v1X - v0X; float e1Y = v1Y - v0Y; float e1Z = v1Z - v0Z;
		float e2X = v2X - v0X; float e2Y = v2Y - v0Y; float e2Z = v2Z - v0Z;

		// begin calculating determinant - also used to calculate U parameter
		float pVecX = dY * e2Z - dZ * e2Y; float pVecY = dZ * e2X - dX * e2Z; float pVecZ = dX * e2Y - dY * e2X;

		// if determinant is near zero, ray lies in plane of triangle
		float det = e1X * pVecX + e1Y * pVecY + e1Z * pVecZ;

		// TEST_CULL = true variant

		if (det < EPSILON) return null;

		// calculate distance from vert0 to ray origin
		float tVecX = oX - v0X; float tVecY = oY - v0Y; float tVecZ = oZ - v0Z;

		// calculate U parameter and test bounds
		float u = (tVecX * pVecX + tVecY * pVecY + tVecZ * pVecZ);
		if (u < 0.0f || u > det) return null;

		// prepare to test V parameter
		float qVecX = tVecY * e1Z - tVecZ * e1Y; float qVecY = tVecZ * e1X - tVecX * e1Z; float qVecZ = tVecX * e1Y - tVecY * e1X;

		// calculate V parameter and test bounds
		float v = (dX * qVecX + dY * qVecY + dZ * qVecZ);
		if (v < 0.0f || u + v > det) return null;

		// calculate t, scale parameters, ray intersects triangle
		float t = (e2X * qVecX + e2Y * qVecY + e2Z * qVecZ);
		t /= det; // in the paper t is multiplied by the inverse of det. that is not necessary here because we only modify t, not u and v

		// return intersection point
		if (t > 0) {
			return t;
		} else {
			return null;
		}
	}

	/**
	 * Möller-Trumbore intersection test
	 * taken from: "Fast, Minimum Storage Ray/Triangle Intersection" (MollerTrumbore97)
	 *
	 * @return the intersection point or null for no intersection
	 */
	public static Float rayTriangleIntersectionNonCulling(
			float oX, float oY, float oZ,
			float dX, float dY, float dZ,
			float v0X, float v0Y, float v0Z,
			float v1X, float v1Y, float v1Z,
			float v2X, float v2Y, float v2Z
	                                                     ) {

		// find vectors for two edges sharing vert0
		float e1X = v1X - v0X; float e1Y = v1Y - v0Y; float e1Z = v1Z - v0Z;
		float e2X = v2X - v0X; float e2Y = v2Y - v0Y; float e2Z = v2Z - v0Z;

		// begin calculating determinant - also used to calculate U parameter
		float pVecX = dY * e2Z - dZ * e2Y; float pVecY = dZ * e2X - dX * e2Z; float pVecZ = dX * e2Y - dY * e2X;

		// if determinant is near zero, ray lies in plane of triangle
		float det = e1X * pVecX + e1Y * pVecY + e1Z * pVecZ;

		// TEST_CULL = false variant

		if (det > -EPSILON && det < EPSILON) return null;
		float invDet = 1.0f / det;

		// calculate distance from vert0 to ray origin
		float tVecX = oX - v0X; float tVecY = oY - v0Y; float tVecZ = oZ - v0Z;

		// calculate U parameter and test bounds
		float u = (tVecX * pVecX + tVecY * pVecY + tVecZ * pVecZ) * invDet;
		if (u < 0.0f || u > 1.0f) return null;

		// prepare to test V parameter
		float qVecX = tVecY * e1Z - tVecZ * e1Y; float qVecY = tVecZ * e1X - tVecX * e1Z; float qVecZ = tVecX * e1Y - tVecY * e1X;

		// calculate V parameter and test bounds
		float v = (dX * qVecX + dY * qVecY + dZ * qVecZ) * invDet;
		if (v < 0.0f || u + v > 1.0f) return null;

		// calculate t, ray intersects triangle
		float t = (e2X * qVecX + e2Y * qVecY + e2Z * qVecZ) * invDet;

		// return intersection point
		if (t > 0) {
			return t;
		} else {
			return null;
		}
	}

	public static Float raySphereIntersection(IReadonlyVector3f orig, IReadonlyVector3f dir, IReadonlyVector3f center, float radius) {
		IVector3f l = center.subtracted(orig);
		float tca = l.dot(dir);
		if (tca < 0) return null;
		float d2 = l.dot(l) - tca * tca;
		if (d2 > radius * radius) return null;
		float thc = (float) Math.sqrt(radius * radius - d2);
		return tca - thc;
	}

	/**
	 * Calculates the area of a triangle with vertices {@code vert0}, {@code vert1} and {@code vert2} using Heron's Formula.
	 *
	 * @param vert0 vertex 0
	 * @param vert1 vertex 1
	 * @param vert2 vertex 2
	 * @return the area of the triangle
	 */
	public static float triangleArea(IReadonlyVector2f vert0, IReadonlyVector2f vert1, IReadonlyVector2f vert2) {
		float ax = vert0.getX() - vert1.getX();
		float ay = vert0.getY() - vert1.getY();
		float bx = vert1.getX() - vert2.getX();
		float by = vert1.getY() - vert2.getY();
		float cx = vert2.getX() - vert0.getX();
		float cy = vert2.getY() - vert0.getY();

		float a = (float) Math.sqrt(ax * ax + ay * ay);
		float b = (float) Math.sqrt(bx * bx + by * by);
		float c = (float) Math.sqrt(cx * cx + cy * cy);
		float s = (a + b + c) / 2;

		return (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	/**
	 * Determines if a vector {@code v} is roughly "between" two vectors {@code v0} and {@code v1}.
	 * The vector {@code v} has to be linearly dependent on the vectors @code v0} and {@code v1} for this test to function correctly.
	 * A vector is considered to be between two other vectors, if it intersects with the line segment that connects these vectors.
	 *
	 * @return true, if the vector {@code v} is roughly "between" two vectors {@code v0} and {@code v1}
	 */
	public static boolean isVectorBetween(IReadonlyVector3f v, IReadonlyVector3f v0, IReadonlyVector3f v1) {
		float vLength = v.getLength();
		float v0Length = v0.getLength();
		float v1Length = v1.getLength();

		float dot01 = v0.dot(v1) / v0Length / v1Length;
		float dot0 = v.dot(v0) / vLength / v0Length;
		float dot1 = v.dot(v1) / vLength / v1Length;

		return dot0 > dot01 && dot1 > dot01;
	}

}
