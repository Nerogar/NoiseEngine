package de.nerogar.noise.util;

public class Vector2f implements Vectorf<Vector2f> {

	private float x;
	private float y;
	private float value;
	private boolean isValueDirty = true;

	//constructors
	public Vector2f(float x, float y) {
		setX(x);
		setY(y);
	}

	public Vector2f(float xy) {
		setX(xy);
		setY(xy);
	}

	public Vector2f() {
	}

	//constructor for cloning
	private Vector2f(float x, float y, float value, boolean valueDirty) {
		this.x = x;
		this.y = y;
		this.value = value;
		this.isValueDirty = valueDirty;
	}

	@Override
	public Vector2f newInstance() {
		return new Vector2f();
	}

	@Override
	public int getComponentCount() {
		return 2;
	}

	//get
	@Override
	public float get(int component) {
		switch (component) {
		case 0:
			return x;
		case 1:
			return y;
		default:
			return 0f;
		}
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	//set	
	@Override
	public Vector2f set(int component, float f) {
		switch (component) {
		case 0:
			setX(f);
			break;
		case 1:
			setY(f);
			break;
		}
		return this;
	}

	public Vector2f setX(float x) {
		this.x = x;
		isValueDirty = true;
		return this;
	}

	public Vector2f setY(float y) {
		this.y = y;
		isValueDirty = true;
		return this;
	}

	@Override
	public Vector2f set(float xy) {
		setX(xy);
		setY(xy);
		return this;
	}

	@Override
	public Vector2f set(Vectorf<?> v) {
		setX(v.get(0));
		setY(v.get(1));
		return this;
	}

	//add
	@Override
	public Vector2f add(int component, float f) {
		switch (component) {
		case 0:
			addX(f);
			break;
		case 1:
			addY(f);
			break;
		}
		return this;
	}

	public Vector2f addX(float x) {
		setX(getX() + x);
		return this;
	}

	public Vector2f addY(float y) {
		setY(getY() + y);
		return this;
	}

	@Override
	public Vector2f add(Vectorf<?> v) {
		addX(v.get(0));
		addY(v.get(1));
		return this;
	}

	@Override
	public Vector2f added(Vectorf<?> v) {
		return clone().add(v);
	}

	//subtract	
	@Override
	public Vector2f subtract(Vectorf<?> v) {
		addX(-v.get(0));
		addY(-v.get(1));
		return this;
	}

	@Override
	public Vector2f subtracted(Vectorf<?> v) {
		return clone().subtract(v);
	}

	//multiply
	@Override
	public Vector2f multiply(float f) {
		setX(getX() * f);
		setY(getY() * f);
		return this;
	}

	@Override
	public Vector2f multiplied(float f) {
		return clone().multiply(f);
	}

	//tools
	@Override
	public Vector2f normalize() {
		return setValue(1f);
	}

	@Override
	public Vector2f normalized() {
		return clone().normalize();
	}

	@Override
	public float getValue() {
		if (isValueDirty) recalculateValue();
		return value;
	}

	@Override
	public float getSquaredValue() {
		return x * x + y * y;
	}

	@Override
	public Vector2f setValue(float value) {
		multiply(value / getValue());
		this.value = value;
		isValueDirty = false;
		return this;
	}

	private void recalculateValue() {
		this.value = (float) Math.sqrt(getX() * getX() + getY() * getY());
		isValueDirty = false;
	}

	@Override
	public Vector2f clone() {
		return new Vector2f(this.x, this.y, this.value, this.isValueDirty);
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}

}
