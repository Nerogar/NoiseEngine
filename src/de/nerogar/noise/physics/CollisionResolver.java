package de.nerogar.noise.physics;

import java.util.List;

import de.nerogar.noise.util.Vectorf;

public class CollisionResolver {

	public static <T extends Vectorf<T>> void resolve(PhysicsBody<T> movingBody, List<InteractingBody<T>> interactingBodies, T interactionVelocity, float timeDelta) {

		for (InteractingBody<T> interactingBody : interactingBodies) {
			//if (!interactingBody.collision) continue;
			//calc impact direction
			/*Vector3f intersectionVolume = new Vector3f();

			for (int axis = 0; axis < tempVelVector.getComponentCount(); axis++) {
				if (tempVelVector.get(axis) < 0) {
					intersectionVolume.set(axis, (collidingBody.getPosition().get(axis) + collidingBody.bounding.b.get(axis)) - (tempPosVector.get(axis) + movingBody.bounding.a.get(axis)));
				} else {
					intersectionVolume.set(axis, (tempPosVector.get(axis) + movingBody.bounding.b.get(axis)) - (collidingBody.getPosition().get(axis) + collidingBody.bounding.a.get(axis)));
				}
				if (intersectionVolume.get(axis) != 0f) intersectionVolume.set(axis, Math.abs(tempVelVector.get(axis)) / intersectionVolume.get(axis));
			}*/

			int intersectionDirection = interactingBody.interactingDirection;
			//int intersectionDirection = VectorTools.getGreatestComponentIndex(intersectionVolume);

			//snap to colliding object

			movingBody.getPosition().set(intersectionDirection, snap(movingBody, interactionVelocity, interactingBody));
			//collision

			//incorrect, but working
			//intersectedBody.getVelocity().add(greatestAxis, movingBody.getVelocity().get(greatestAxis) * movingBody.inverseMass);
			//movingBody.getVelocity().set(greatestAxis, 0f);

			//correct, but not working

			if (interactingBody.body.isStaticInAxis(intersectionDirection)) {
				float k = interactingBody.body.stiffness * movingBody.stiffness;
				//k = 1f;

				float newVelMoving = (movingBody.getVelocity().get(intersectionDirection) * -k);

				movingBody.getVelocity().set(intersectionDirection, newVelMoving);

			} else {
				float k = interactingBody.body.stiffness * movingBody.stiffness;

				//TODO remove
				//k = 1.0f;

				float newVelMoving = (movingBody.getForce().get(intersectionDirection) + interactingBody.body.getForce().get(intersectionDirection) - ((movingBody.getVelocity().get(intersectionDirection) - interactingBody.body
						.getVelocity().get(intersectionDirection)) * interactingBody.body.mass * k)) / (movingBody.mass + interactingBody.body.mass);
				float newVelInters = (movingBody.getForce().get(intersectionDirection) + interactingBody.body.getForce().get(intersectionDirection) - ((interactingBody.body.getVelocity().get(intersectionDirection) - movingBody
						.getVelocity().get(intersectionDirection)) * movingBody.mass * k)) / (movingBody.mass + interactingBody.body.mass);

				movingBody.getVelocity().set(intersectionDirection, newVelMoving);
				interactingBody.body.getVelocity().set(intersectionDirection, newVelInters);

			}
		}

		for (InteractingBody<T> interactingBody : interactingBodies) {

			//friction
			T frictionForce = interactionVelocity.newInstance();
			float f = interactingBody.body.friction * movingBody.friction;
			//f = (float) Math.pow(f, timeDelta);
			f = 1;
			f *= timeDelta;

			//movingBody.getVelocity().multiply(f);
			frictionForce.set(movingBody.getForce()).set(interactingBody.interactingDirection, 0f).multiply(-f);
			//frictionForce.set(movingBody.getForce()).set(greatestAxis, 0f).multiply(-1f).multiply(f);
			if (frictionForce.getSquaredValue() > 1) System.out.println(frictionForce);
			movingBody.addForce(frictionForce);
			//movingBody.getVelocity().multiply(1f - ((1f - f) * timeDelta));
		}
	}

	public static <T extends Vectorf<T>> float snap(PhysicsBody<T> movingBody, T velocity, InteractingBody<T> snapBody) {
		int direction = snapBody.interactingDirection;

		if (velocity.get(direction) < 0) {
			//movingBody.getPosition().set(direction, snapBody.body.getPosition().get(direction) + snapBody.body.bounding.b.get(direction) - movingBody.bounding.a.get(direction));
			return snapBody.body.getPosition().get(direction) + snapBody.body.bounding.b.get(direction) - movingBody.bounding.a.get(direction);

		} else {
			//movingBody.getPosition().set(direction, snapBody.body.getPosition().get(direction) + snapBody.body.bounding.a.get(direction) - movingBody.bounding.b.get(direction));
			return snapBody.body.getPosition().get(direction) + snapBody.body.bounding.a.get(direction) - movingBody.bounding.b.get(direction);
		}
	}

}
