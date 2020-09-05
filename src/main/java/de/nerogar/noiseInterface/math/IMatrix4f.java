package de.nerogar.noiseInterface.math;

public interface IMatrix4f extends IReadonlyMatrix4f {

	IMatrix4f set(int lineIndex, int columnIndex, float f);

	IMatrix4f set(float allComponents);

	IMatrix4f set(IReadonlyMatrix4f m);

	IMatrix4f set(float[] m);

	IMatrix4f set(float c0, float c1, float c2, float c3, float c4, float c5, float c6, float c7, float c8, float c9, float c10, float c11, float c12, float c13, float c14, float c15);

	IMatrix4f add(IReadonlyMatrix4f m);

	IMatrix4f subtract(IReadonlyMatrix4f m);

	IMatrix4f multiply(float f);

	IMatrix4f multiplyRight(IReadonlyMatrix4f m);

	IMatrix4f multiplyLeft(IReadonlyMatrix4f m);

	IMatrix4f invert();

	IMatrix4f transpose();

}
