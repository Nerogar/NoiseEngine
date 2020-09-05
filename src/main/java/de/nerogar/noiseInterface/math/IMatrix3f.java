package de.nerogar.noiseInterface.math;

public interface IMatrix3f extends IReadonlyMatrix3f {

	IMatrix3f set(int lineIndex, int columnIndex, float f);

	IMatrix3f set(float allComponents);

	IMatrix3f set(IReadonlyMatrix3f m);

	IMatrix3f set(float[] m);

	IMatrix3f set(float c0, float c1, float c2, float c3, float c4, float c5, float c6, float c7, float c8);

	IMatrix3f add(IReadonlyMatrix3f m);

	IMatrix3f subtract(IReadonlyMatrix3f m);

	IMatrix3f multiply(float f);

	IMatrix3f multiplyRight(IReadonlyMatrix3f m);

	IMatrix3f multiplyLeft(IReadonlyMatrix3f m);

	IVector3f multiply(IVector3f v);

	IMatrix3f invert();

	IMatrix3f transpose();

}
