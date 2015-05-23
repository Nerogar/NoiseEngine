package de.nerogar.noise.util;

public interface Matrixf<T extends Matrixf<T>> {

	public abstract int getComponentCount();

	public abstract T newInstance();

	//get
	public abstract float get(int componentLine, int componentCollumn);

	//set
	public abstract T set(int componentLine, int componentCollumn, float f);

	public abstract T set(float allComponents);

	public abstract T set(Matrixf<?> m);

	public abstract T set(float[] m);

	//add
	public abstract T add(Matrixf<?> m);

	public abstract T added(Matrixf<?> m);

	//subtract
	public abstract T subtract(Matrixf<?> m);

	public abstract T subtracted(Matrixf<?> m);

	//multiply
	public abstract T multiply(float f);

	public abstract T multiplied(float f);

	public abstract T multiplyRight(Matrixf<?> m);

	public abstract T multipliedRight(Matrixf<?> m);

	public abstract T multiplyLeft(Matrixf<?> m);

	public abstract T multipliedLeft(Matrixf<?> m);

	//tools
	public abstract T clone();

}
