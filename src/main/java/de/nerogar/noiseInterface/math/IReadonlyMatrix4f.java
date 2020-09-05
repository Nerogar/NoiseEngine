package de.nerogar.noiseInterface.math;

import de.nerogar.noise.math.Matrix4f;

import java.nio.FloatBuffer;

public interface IReadonlyMatrix4f {

	int getComponentCount();

	IMatrix4f newInstance();

	float get(int lineIndex, int columnIndex);

	IMatrix4f added(IReadonlyMatrix4f m);

	IMatrix4f subtracted(IReadonlyMatrix4f m);

	IMatrix4f multiplied(float f);

	IMatrix4f multipliedRight(IReadonlyMatrix4f m);

	IMatrix4f multipliedLeft(IReadonlyMatrix4f m);

	FloatBuffer asBuffer();

	IMatrix4f inverted();

	IMatrix4f transposed();

	IMatrix4f clone();
}
