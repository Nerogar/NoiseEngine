package de.nerogar.noise.util;

public class MathHelper {

	private static final float EPSILON = 0.000001f;

	/*private static final int LOOKUP_TABLE_LENGTH = 4096;
	private static float[] sinTable;

	private static final float HALF_PI = (float) (Math.PI * 0.5);
	public static final float PI = (float) (Math.PI);
	public static final float TAU = (float) (Math.PI * 2);
	public static final float INVERSE_TAU = 1.0f / TAU;

	public static float sin(float radiant) {
		float x = (radiant % TAU);
		if (x < 0) x += TAU;
		x *= INVERSE_TAU;
		return sinTable[(int) (x * LOOKUP_TABLE_LENGTH)];
	}

	public static float cos(float radiant) {
		return sin(radiant + HALF_PI);
	}

	static {
		sinTable = new float[LOOKUP_TABLE_LENGTH];

		for (int i = 0; i < LOOKUP_TABLE_LENGTH; i++) {
			sinTable[i] = (float) Math.sin((TAU) * ((float) i / LOOKUP_TABLE_LENGTH));
		}
	}*/

	public static float clamp(float val, float min, float max) {
		return Math.min(Math.max(min, val), max);
	}

	public static int clamp(int val, int min, int max) {
		return Math.min(Math.max(min, val), max);
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
			return point.subtracted(ray.getStart()).getValue();
		} else {
			float dx = point.getX() - (ray.getStart().getX() + t0 * ray.getDir().getX());
			float dy = point.getY() - (ray.getStart().getY() + t0 * ray.getDir().getY());
			float dz = point.getZ() - (ray.getStart().getZ() + t0 * ray.getDir().getZ());

			return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		}
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

}
