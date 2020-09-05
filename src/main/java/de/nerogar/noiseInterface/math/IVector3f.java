package de.nerogar.noiseInterface.math;

import de.nerogar.noise.math.Vector3f;

public interface IVector3f extends IReadonlyVector3f {

	// set
	IVector3f set(int component, float f);

	IVector3f set(float x, float y, float z);

	IVector3f setX(float x);

	IVector3f setY(float y);

	IVector3f setZ(float z);

	IVector3f set(float xyz);

	IVector3f set(IReadonlyVector3f v);

	// add
	IVector3f add(int component, float f);

	IVector3f addX(float x);

	IVector3f addY(float y);

	IVector3f addZ(float z);

	IVector3f add(IReadonlyVector3f v);

	// subtract
	IVector3f subtract(IReadonlyVector3f v);

	// multiply
	IVector3f multiply(float f);

	// tools
	IVector3f cross(IReadonlyVector3f v);

	void reflect(IReadonlyVector3f v);

	IVector3f normalize();

	IVector3f setLength(float length);

	IVector3f transform(IMatrix4f m);

	IVector3f transform(IMatrix4f m, float w);
}
