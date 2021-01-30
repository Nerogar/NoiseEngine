package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IQuaternion;
import de.nerogar.noiseInterface.math.IReadonlyQuaternion;
import de.nerogar.noiseInterface.math.IVector3f;

public class Quaternion implements IQuaternion {

	private float x;
	private float y;
	private float z;
	private float w;

	public Quaternion() {
		this(0, 0, 0);
	}

	public Quaternion(float x, float y, float z) {
		this(x, y, z, 1.0f - x * x - y * y - z * z);
	}

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public IVector3f newInstance() {
		return null;
	}

	@Override
	public float get(int component) {
		switch (component) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			case 3:
				return w;
			default:
				return 0f;
		}
	}

	@Override
	public float getX() { return x; }

	@Override
	public float getY() { return y; }

	@Override
	public float getZ() { return z; }

	@Override
	public float getW() { return w; }

	@Override
	public IQuaternion set(int component, float f) {
		switch (component) {
			case 0:
				x = f;
				break;
			case 1:
				y = f;
				break;
			case 2:
				z = f;
				break;
			case 3:
				w = f;
				break;
		}

		return this;
	}

	@Override
	public IQuaternion set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1.0f - x * x - y * y - z * z;
		return this;
	}

	@Override
	public IQuaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	@Override
	public IQuaternion setX(float x) {
		this.x = x;
		return this;
	}

	@Override
	public IQuaternion setY(float y) {
		this.y = y;
		return this;
	}

	@Override
	public IQuaternion setZ(float z) {
		this.z = z;
		return this;
	}

	@Override
	public IQuaternion setW(float w) {
		this.w = w;
		return this;
	}

	@Override
	public IQuaternion set(IReadonlyQuaternion q) {
		x = q.getX();
		y = q.getY();
		z = q.getZ();
		w = q.getW();
		return this;
	}

	@Override
	public IQuaternion normalize() {
		float norm = (float) Math.sqrt(x * x + y * y + z * z + w * w);
		this.x /= norm;
		this.y /= norm;
		this.z /= norm;
		this.w /= norm;
		return this;
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + "|" + z + "|" + w + ")";
	}
}
