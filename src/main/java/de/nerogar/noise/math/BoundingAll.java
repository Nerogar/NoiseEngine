package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IBounding;
import de.nerogar.noiseInterface.math.IReadonlyVector3f;

public class BoundingAll implements IBounding {

	private final Vector3f origin;

	/**
	 * create a new bounding that collides with everything
	 */
	public BoundingAll() {
		origin = new Vector3f();
	}

	@Override
	public IReadonlyVector3f point() {
		return origin;
	}

	@Override
	public boolean hasPoint(float x, float y, float z) {
		return true;
	}

	@Override
	public boolean isInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return false;
	}

	@Override
	public boolean hasInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return true;
	}

	@Override
	public boolean overlaps(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return true;
	}

	@Override
	public boolean intersectsXPlane(float xValue) {
		return true;
	}

	@Override
	public boolean intersectsYPlane(float yValue) {
		return true;
	}

	@Override
	public boolean intersectsZPlane(float zValue) {
		return true;
	}

	@Override
	public String toString() {
		return "all";
	}

	@Override
	public IBounding clone() {
		return new BoundingAll();
	}

}
