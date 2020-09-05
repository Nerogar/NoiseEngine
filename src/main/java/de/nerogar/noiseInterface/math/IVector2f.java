package de.nerogar.noiseInterface.math;

public interface IVector2f extends IReadonlyVector2f {

	// set
	IVector2f set(int component, float f);

	IVector2f set(float x, float y);

	IVector2f setX(float x);

	IVector2f setY(float y);

	IVector2f set(float xy);

	IVector2f set(IReadonlyVector2f v);

	// add
	IVector2f add(int component, float f);

	IVector2f addX(float x);

	IVector2f addY(float y);

	IVector2f add(IReadonlyVector2f v);

	// subtract
	IVector2f subtract(IReadonlyVector2f v);

	// multiply
	IVector2f multiply(float f);

	// tools
	void reflect(IReadonlyVector2f v);

	IVector2f normalize();

	IVector2f setLength(float length);

	IVector2f transform(IMatrix4f m);

	IVector2f transform(IMatrix4f m, float z, float w);
}
