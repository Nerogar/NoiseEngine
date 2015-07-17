package de.nerogar.noise.physics;

import de.nerogar.noise.util.Vectorf;

public class RayIntersection<T extends Vectorf<T>> implements Comparable<RayIntersection<T>> {
	public T intersectionPoint;
	public float distance;
	public PhysicsBody<T> intersectingBody;

	public RayIntersection(T intersectionPoint, float distance, PhysicsBody<T> intersectingBody) {
		this.intersectionPoint = intersectionPoint;
		this.distance = distance;
		this.intersectingBody = intersectingBody;
	}

	@Override
	public int compareTo(RayIntersection<T> obj) {
		return distance - obj.distance < 0 ? -1 : 1;
	}

}
