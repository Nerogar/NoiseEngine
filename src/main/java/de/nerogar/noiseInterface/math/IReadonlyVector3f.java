package de.nerogar.noiseInterface.math;

public interface IReadonlyVector3f {

	IVector3f newInstance();

	int getComponentCount();

	// get
	float get(int component);

	float getX();

	float getY();

	float getZ();

	IVector3f added(IReadonlyVector3f v);

	IVector3f subtracted(IReadonlyVector3f v);

	IVector3f multiplied(float f);

	// tools
	float dot(IReadonlyVector3f v);

	IVector3f crossed(IReadonlyVector3f v);

	IVector3f normalized();

	float getLength();

	float getSquaredLength();

	IVector3f transformed(IReadonlyMatrix4f m);

	IVector3f transformed(IReadonlyMatrix4f m, float w);

	IVector3f clone();
}
