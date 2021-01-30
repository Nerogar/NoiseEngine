package de.nerogar.noiseInterface.math;

public interface IReadonlyQuaternion {

	IVector3f newInstance();

	// get
	float get(int component);

	float getX();

	float getY();

	float getZ();

	float getW();

}
