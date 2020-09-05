package de.nerogar.noise.util;

public interface IReadonlyVectorf<T extends IReadonlyVectorf<T>> {

	public abstract int getComponentCount();

	public abstract T newInstance();

	//get
	public abstract float get(int component);

	public abstract T added(IReadonlyVectorf<?> v);

	public abstract T subtracted(IReadonlyVectorf<?> v);

	public abstract T multiplied(float f);

	//tools
	public abstract float dot(IReadonlyVectorf<?> v);

	public abstract T normalized();

	public abstract float getValue();

	public abstract float getSquaredValue();

	public abstract T clone();

}
