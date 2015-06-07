package de.nerogar.noise.util;

public interface Matrixf<T extends Matrixf<T>> {

	public abstract int getComponentCount();

	public abstract T newInstance();

	//get
	public abstract float get(int componentLine, int componentCollumn);

	//set
	public abstract T set(int componentLine, int componentCollumn, float f);

	public abstract T set(float allComponents);

	public abstract T set(T m);

	public abstract T set(float[] m);

	//add
	public abstract T add(T m);

	public abstract T added(T m);

	//subtract
	public abstract T subtract(T m);

	public abstract T subtracted(T m);

	//multiply
	public abstract T multiply(float f);

	public abstract T multiplied(float f);

	public abstract T multiplyRight(T m);

	public abstract T multipliedRight(T m);

	public abstract T multiplyLeft(T m);

	public abstract T multipliedLeft(T m);

	//tools
	public abstract T clone();

}
