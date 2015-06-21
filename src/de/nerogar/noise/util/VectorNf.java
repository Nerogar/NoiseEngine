package de.nerogar.noise.util;

public class VectorNf implements Vectorf<VectorNf> {

	private float[] components;
	private float value;
	private boolean isValueDirty = true;

	//constructors
	public VectorNf(float... components) {
		set(components);
	}

	public VectorNf(int componentCount) {
		components = new float[componentCount];
	}

	//constructor for cloning
	private VectorNf(float[] components, float value, boolean valueDirty) {
		this.components = components;
		this.value = value;
		this.isValueDirty = valueDirty;
	}

	@Override
	public VectorNf newInstance() {
		return new VectorNf(components.length);
	}

	@Override
	public int getComponentCount() {
		return components.length;
	}

	//get
	@Override
	public float get(int component) {
		return components[component];
	}

	//set	
	@Override
	public VectorNf set(int component, float f) {
		components[component] = f;
		return this;
	}

	@Override
	public VectorNf set(float f) {
		for (int i = 0; i < components.length; i++) {
			components[i] = f;
		}
		return this;
	}

	@Override
	public VectorNf set(Vectorf<?> v) {
		for (int i = 0; i < components.length; i++) {
			components[i] = v.get(i);
		}
		return this;
	}

	public VectorNf set(float... components) {
		for (int i = 0; i < components.length; i++) {
			this.components[i] = components[i];
		}
		return this;
	}

	//add
	@Override
	public VectorNf add(int component, float f) {
		components[component] += f;
		return this;
	}

	@Override
	public VectorNf add(Vectorf<?> v) {
		for (int i = 0; i < components.length; i++) {
			components[i] += v.get(i);
		}
		return this;
	}

	@Override
	public VectorNf added(Vectorf<?> v) {
		return clone().add(v);
	}

	//subtract	
	@Override
	public VectorNf subtract(Vectorf<?> v) {
		for (int i = 0; i < components.length; i++) {
			components[i] -= v.get(i);
		}
		return this;
	}

	@Override
	public VectorNf subtracted(Vectorf<?> v) {
		return clone().subtract(v);
	}

	//multiply
	@Override
	public VectorNf multiply(float f) {
		for (int i = 0; i < components.length; i++) {
			components[i] *= f;
		}
		return this;
	}

	@Override
	public VectorNf multiplied(float f) {
		return clone().multiply(f);
	}

	//tools
	@Override
	public float dot(Vectorf<?> v) {
		float dot = 0;

		for (int i = 0; i < components.length; i++) {
			dot += components[i] * v.get(i);
		}

		return dot;
	}

	@Override
	public void reflect(Vectorf<?> v) {
		float dot = 2.0f * dot(v);

		for (int i = 0; i < getComponentCount(); i++) {
			set(i, get(i) - dot * v.get(0));
		}
	}

	@Override
	public VectorNf normalize() {
		return setValue(1f);
	}

	@Override
	public VectorNf normalized() {
		return clone().normalize();
	}

	@Override
	public float getValue() {
		if (isValueDirty) recalculateValue();
		return value;
	}

	@Override
	public float getSquaredValue() {
		float sum = 0;
		for (int i = 0; i < components.length; i++) {
			sum += components[i] * components[i];
		}
		return sum;
	}

	@Override
	public VectorNf setValue(float value) {
		multiply(value / getValue());
		this.value = value;
		isValueDirty = false;
		return this;
	}

	private void recalculateValue() {
		this.value = (float) Math.sqrt(getSquaredValue());
		isValueDirty = false;
	}

	@Override
	public VectorNf clone() {
		return new VectorNf(components, this.value, this.isValueDirty);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i < components.length - 1; i++) {
			sb.append(components[i]).append("|");
		}
		sb.append(components[components.length - 1]).append(")");
		return sb.toString();
	}

}
