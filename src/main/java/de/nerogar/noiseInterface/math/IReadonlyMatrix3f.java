package de.nerogar.noiseInterface.math;

import de.nerogar.noise.math.Matrix3f;
import de.nerogar.noise.math.Vector3f;

import java.nio.FloatBuffer;

public interface IReadonlyMatrix3f {

	int getComponentCount();

	Matrix3f newInstance();

	float get(int lineIndex, int columnIndex);

	Matrix3f added(IReadonlyMatrix3f m);

	Matrix3f subtracted(IReadonlyMatrix3f m);

	Matrix3f multiplied(float f);

	Matrix3f multipliedRight(IReadonlyMatrix3f m);

	Matrix3f multipliedLeft(IReadonlyMatrix3f m);

	Vector3f multiplied(IReadonlyVector3f v);

	FloatBuffer asBuffer();

	Matrix3f inverted();

	Matrix3f transposed();

	IMatrix3f clone();
}
