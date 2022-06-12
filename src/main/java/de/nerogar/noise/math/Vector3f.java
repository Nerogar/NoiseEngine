package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.*;

public class Vector3f implements IVector3f {

	public static final IReadonlyVector3f ZERO = new Vector3f();

	private static final float SQRT_3 = (float) Math.sqrt(3.0);

	private float   x;
	private float   y;
	private float   z;
	private float   length;
	private boolean isLengthDirty = true;

	// constructors
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		isLengthDirty = true;
	}

	public Vector3f(float xyz) {
		set(xyz);
	}

	public Vector3f() {
	}

	// copy constructor
	private Vector3f(float x, float y, float z, float length, boolean isLengthDirty) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.isLengthDirty = isLengthDirty;
	}

	@Override
	public Vector3f newInstance() {
		return new Vector3f();
	}

	@Override
	public int getComponentCount() {
		return 3;
	}

	// get
	@Override
	public float get(int component) {
		switch (component) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			default:
				return 0f;
		}
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	// set
	@Override
	public Vector3f set(int component, float f) {
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
		}
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f setX(float x) {
		this.x = x;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f setY(float y) {
		this.y = y;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f setZ(float z) {
		this.z = z;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f set(float xyz) {
		x = xyz;
		y = xyz;
		z = xyz;
		setValueCache(Math.abs(xyz) * SQRT_3);
		return this;
	}

	@Override
	public Vector3f set(IReadonlyVector3f v) {
		x = v.getX();
		y = v.getY();
		z = v.getZ();
		isLengthDirty = true;
		return this;
	}

	// add
	@Override
	public Vector3f add(int component, float f) {
		switch (component) {
			case 0:
				x += f;
				break;
			case 1:
				y += f;
				break;
			case 2:
				z += f;
				break;
		}
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f addX(float x) {
		this.x += x;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f addY(float y) {
		this.y += y;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f addZ(float z) {
		this.z += z;
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f add(IReadonlyVector3f v) {
		x += v.getX();
		y += v.getY();
		z += v.getZ();
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f added(IReadonlyVector3f v) {
		return clone().add(v);
	}

	// subtract
	@Override
	public Vector3f subtract(IReadonlyVector3f v) {
		x -= v.getX();
		y -= v.getY();
		z -= v.getZ();
		isLengthDirty = true;
		return this;
	}

	@Override
	public Vector3f subtracted(IReadonlyVector3f v) {
		return clone().subtract(v);
	}

	// multiply
	@Override
	public Vector3f multiply(float f) {
		length *= f;
		x *= f;
		y *= f;
		z *= f;
		return this;
	}

	@Override
	public Vector3f multiplied(float f) {
		return clone().multiply(f);
	}

	// tools
	@Override
	public float dot(IReadonlyVector3f v) {
		return x * v.getX() + y * v.getY() + z * v.getZ();
	}

	@Override
	public Vector3f cross(IReadonlyVector3f v) {
		set(
				y * v.getZ() - z * v.getY(),
				z * v.getX() - x * v.getZ(),
				x * v.getY() - y * v.getX()
		   );

		return this;
	}

	@Override
	public Vector3f crossed(IReadonlyVector3f v) {
		return new Vector3f(
				y * v.getZ() - z * v.getY(),
				z * v.getX() - x * v.getZ(),
				x * v.getY() - y * v.getX()
		);
	}

	@Override
	public void reflect(IReadonlyVector3f v) {
		float dot = 2.0f * dot(v);

		set(
				getX() - dot * v.getX(),
				getY() - dot * v.getY(),
				getZ() - dot * v.getZ()
		   );
	}

	@Override
	public Vector3f normalize() {
		return setLength(1f);
	}

	@Override
	public Vector3f normalized() {
		return clone().normalize();
	}

	@Override
	public float getLength() {
		if (isLengthDirty) recalculateValue();
		return length;
	}

	@Override
	public float getSquaredLength() {
		return x * x + y * y + z * z;
	}

	@Override
	public Vector3f setLength(float length) {
		float oldLength = getLength();
		if (oldLength != 0) {
			multiply(length / oldLength);
			this.length = length;
			isLengthDirty = false;
		}
		return this;
	}

	@Override
	public Vector3f transform(IReadonlyMatrix4f m) {
		float x = this.x;
		float y = this.y;
		float z = this.z;

		return set(
				x * m.get(0, 0) + y * m.get(0, 1) + z * m.get(0, 2),
				x * m.get(1, 0) + y * m.get(1, 1) + z * m.get(1, 2),
				x * m.get(2, 0) + y * m.get(2, 1) + z * m.get(2, 2)
		          );

	}

	@Override
	public Vector3f transform(IReadonlyMatrix4f m, float w) {
		float x = this.x;
		float y = this.y;
		float z = this.z;

		return set(
				x * m.get(0, 0) + y * m.get(0, 1) + z * m.get(0, 2) + w * m.get(0, 3),
				x * m.get(1, 0) + y * m.get(1, 1) + z * m.get(1, 2) + w * m.get(1, 3),
				x * m.get(2, 0) + y * m.get(2, 1) + z * m.get(2, 2) + w * m.get(2, 3)
		          );

	}

	@Override
	public Vector3f transformed(IReadonlyMatrix4f m) {
		return clone().transform(m);
	}

	@Override
	public Vector3f transformed(IReadonlyMatrix4f m, float w) {
		return clone().transform(m, w);
	}

	private void recalculateValue() {
		setValueCache((float) Math.sqrt(getSquaredLength()));
	}

	private void setValueCache(float value) {
		this.length = value;
		isLengthDirty = false;
	}

	@Override
	public Vector3f clone() {
		return new Vector3f(x, y, z, length, isLengthDirty);
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + "|" + z + ")";
	}

	@Override
	public boolean equals(Object o) {
		// generated by IntelliJ IDEA
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Vector3f vector3f = (Vector3f) o;

		if (Float.compare(vector3f.x, x) != 0) return false;
		if (Float.compare(vector3f.y, y) != 0) return false;
		return Float.compare(vector3f.z, z) == 0;
	}

	@Override
	public int hashCode() {
		// generated by IntelliJ IDEA
		int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
		result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
		result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
		return result;
	}

}
