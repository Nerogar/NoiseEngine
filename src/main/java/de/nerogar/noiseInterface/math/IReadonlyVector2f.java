package de.nerogar.noiseInterface.math;

public interface IReadonlyVector2f {

	IVector2f newInstance();

	int getComponentCount();

	// get
	float get(int component);

	float getX();

	float getY();

	IVector2f added(IReadonlyVector2f v);

	IVector2f subtracted(IReadonlyVector2f v);

	IVector2f multiplied(float f);

	// tools
	float dot(IReadonlyVector2f v);

	IVector2f normalized();

	float getLength();

	float getSquaredLength();

	IVector2f transformed(IMatrix4f m);

	IVector2f transformed(IMatrix4f m, float z, float w);

	IVector2f clone();
}
