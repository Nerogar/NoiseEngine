package de.nerogar.noiseInterface.math;

public interface IQuaternion extends IReadonlyQuaternion {
	// set
	IQuaternion set(int component, float f);

	IQuaternion set(float x, float y, float z);

	IQuaternion set(float x, float y, float z, float w);

	IQuaternion setX(float x);

	IQuaternion setY(float y);

	IQuaternion setZ(float z);

	IQuaternion setW(float w);

	IQuaternion set(IReadonlyQuaternion q);

	// tools
	IQuaternion normalize();
}
