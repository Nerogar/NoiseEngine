package de.nerogar.noise.physics;

import de.nerogar.noise.util.Vectorf;

public class Ray<T extends Vectorf<T>> {
	private T start;
	/**normalized direction*/
	private T direction;
	private T inverseDirection;

	//private static Vector3f intersectionPoint = new Vector3f();

	public Ray(T start, T direction) {
		this.start = start;
		setDirection(direction);
	}

	public RayIntersection<T> getIntersectionPoint(PhysicsBody<T> body) {
		T intersectionPoint = start.newInstance();

		for (int axis = 0; axis < direction.getComponentCount(); axis++) {

			if (direction.get(axis) != 0f) {
				float intersectionPlane;
				if (direction.get(axis) < 0) {
					intersectionPlane = body.getPosition().get(axis) + body.bounding.b.get(axis);
				} else {
					intersectionPlane = body.getPosition().get(axis) + body.bounding.a.get(axis);
				}

				intersectionPoint.set(direction);
				float distance = (intersectionPlane - start.get(axis)) * inverseDirection.get(axis);
				intersectionPoint.multiply(distance).add(start);

				boolean isValidIntersection = true;

				for (int i = 0; i < direction.getComponentCount(); i++) {
					if (i != axis) {
						if (intersectionPoint.get(i) >= body.getPosition().get(i) + body.bounding.b.get(i) || intersectionPoint.get(i) <= body.getPosition().get(i) + body.bounding.a.get(i)) {
							isValidIntersection = false;
							break;
						}
					}
				}

				if (isValidIntersection) return new RayIntersection<T>(intersectionPoint, distance, body);
			}
		}

		return null;
	}

	public void setDirection(T direction) {
		this.direction = direction;
		if (inverseDirection == null) inverseDirection = direction.newInstance();
		for (int i = 0; i < direction.getComponentCount(); i++) {
			inverseDirection.set(i, 1f / direction.get(i));
		}

	}

	public void setStart(T start) {
		this.start = start;
	}

	public T getStart() {
		return start;
	}

	public T getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "[start: " + start + " direction: " + direction + "]";
	}

	/*public RayIntersection getIntersectionPoint(PhysicsBody body) {
		Vector3f intersectionPoint = new Vector3f();
		for (int axis = 0; axis < direction.getComponentCount(); axis++) {

			if (direction.get(axis) != 0f) {
				float intersectionPlane;
				if (direction.get(axis) < 0) {
					intersectionPlane = body.getPosition().get(axis) + body.bounding.b.get(axis);
				} else {
					intersectionPlane = body.getPosition().get(axis) + body.bounding.a.get(axis);
				}

				intersectionPoint.set(direction);
				float distance = (intersectionPlane - start.get(axis)) / direction.get(axis);
				intersectionPoint.multiply(distance).add(start);

				boolean isValidIntersection = true;

				for (int i = 0; i < direction.getComponentCount(); i++) {
					if (i != axis) {
						if (intersectionPoint.get(i) >= body.getPosition().get(i) + body.bounding.b.get(i) || intersectionPoint.get(i) <= body.getPosition().get(i) + body.bounding.a.get(i)) isValidIntersection = false;
					}
				}

				if (isValidIntersection) return new RayIntersection(intersectionPoint, distance, body);
			}
		}

		return null;
	}*/

}
