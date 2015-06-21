package de.nerogar.noise.util;

public interface Vectorf<T extends Vectorf<T>> {

	public abstract int getComponentCount();

	public abstract T newInstance();

	//get
	public abstract float get(int component);

	//set
	public abstract T set(int component, float f);

	public abstract T set(float allComponents);

	public abstract T set(Vectorf<?> v);

	//add
	public abstract T add(int component, float f);

	public abstract T add(Vectorf<?> v);

	public abstract T added(Vectorf<?> v);

	//subtract
	public abstract T subtract(Vectorf<?> v);

	public abstract T subtracted(Vectorf<?> v);

	//multiply
	public abstract T multiply(float f);

	public abstract T multiplied(float f);

	//tools
	public abstract float dot(Vectorf<?> v);

	/**
	 * Reflect this vector at the plane with the normal v.
	 * v has to be normalized.
	 * 
	 * @param v the normal of the plane for reflection
	 */
	public void reflect(Vectorf<?> v);

	public abstract T normalize();

	public abstract T normalized();

	public abstract float getValue();

	public abstract float getSquaredValue();

	public abstract T setValue(float value);

	public abstract T clone();

}
