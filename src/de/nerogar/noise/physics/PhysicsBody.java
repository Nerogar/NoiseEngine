package de.nerogar.noise.physics;

import de.nerogar.noise.util.Vectorf;

public class PhysicsBody<T extends Vectorf<T>> {
	public BoundingAABB<T> bounding;
	private T position;

	private T velocity;
	private T force;

	public float mass;
	protected float inverseMass;
	public float friction; // 1 = no friction; 0 = all the friction
	public float stiffness; // used for collisions 0 = no bouncing; 1 = all the bouncing

	private boolean isStatic;
	private boolean[] staticAxis;

	private boolean removeFromWorld;

	public PhysicsBody(BoundingAABB<T> bounding, T position) {
		this.bounding = bounding;
		this.position = position;
		this.velocity = position.newInstance();
		this.force = position.newInstance();
		staticAxis = new boolean[position.getComponentCount()];

		this.mass = 1f;
		this.inverseMass = 1f;

		this.friction = 0.9f;
		this.stiffness = 0.5f;
	}

	public boolean intersects(PhysicsBody<T> body) {
		return bounding.intersects(body.bounding, position, body.position);
	}

	public boolean intersects(T point) {
		return bounding.intersects(point, position);
	}

	public void setMass(float mass) {
		this.mass = mass;
		this.inverseMass = 1f / mass;
	}

	public void addForce(T newForce) {
		velocity.add(newForce.multiplied(inverseMass));
	}

	public T getPosition() {
		return position;
	}

	public T getForce() {
		recalculateForce();
		return force;
	}

	private void recalculateForce() {
		force.set(velocity.multiplied(mass));
	}

	public T getVelocity() {
		return velocity;
	}

	public void clearStaticAxis() {
		for (int i = 0; i < staticAxis.length; i++) {
			staticAxis[i] = false;
		}
	}

	public void setStaticInAxis(int axis) {
		staticAxis[axis] = true;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isStaticInAxis(int axis) {
		return (isStatic) ? true : staticAxis[axis];
	}

	public void removeFromWorld() {
		removeFromWorld = true;
	}

	protected void resetRemoveFromWorld() {
		removeFromWorld = false;
	}

	public boolean markedToRemoveFromWorld() {
		return removeFromWorld;
	}

}
