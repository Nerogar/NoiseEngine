package de.nerogar.noise.physics;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.util.*;

public class PhysicsSpace<T extends Vectorf<T>> {
	private final T DIMENSION;

	private ArrayList<PhysicsBody<T>> staticBodys;
	private ArrayList<PhysicsBody<T>> bodies;

	private final float NOT_MOVING_THRESHOLD = 0.001f;

	//TODO remove
	public Vectorf<?> gravity = new Vector3f(0f, -10f, 0f);
	private Texture2D debugTexture;

	public PhysicsSpace(T dimension) {
		this.DIMENSION = dimension;
		staticBodys = new ArrayList<PhysicsBody<T>>();
		bodies = new ArrayList<PhysicsBody<T>>();

		//debugTexture = TextureLoader.loadTexture("res/textures/test3.png", "debug");
	}

	public void addStaticBody(PhysicsBody<T> staticBody) {
		staticBody.setStatic(true);
		staticBodys.add(staticBody);
	}

	public void addBody(PhysicsBody<T> body) {
		body.setStatic(false);
		bodies.add(body);
	}

	public void clear() {
		staticBodys.clear();
		bodies.clear();
	}

	public void update(float timeDelta) {
		//TODO debug start
		for (PhysicsBody<T> pb : bodies) {
			//System.out.print(pb.getPosition().get(1) + ";  " + pb.isStaticInAxis(1) + " | ");
		}
		//System.out.println();
		//debug end

		T resetPos = DIMENSION.newInstance(); //to reset after intersection calculation
		T calculationVel = DIMENSION.newInstance(); //to calculate collisions

		for (PhysicsBody<T> body : bodies) {
			//TODO calc static (broken)
			body.clearStaticAxis();

			resetPos.set(body.getPosition());

			//TODO add all modifier
			body.getVelocity().add((getGravity().multiplied(timeDelta)));
			body.getPosition().add(body.getVelocity().multiplied(timeDelta));

			calculationVel.set(body.getVelocity());
			ArrayList<PhysicsBody<T>> frictionBodies = getIntersecting(body); //all colliding bodies
			ArrayList<InteractingBody<T>> interactingBodies = new ArrayList<InteractingBody<T>>(); //all colliding bodies in a wrapper class

			for (PhysicsBody<T> frictionBody : frictionBodies) {
				InteractingBody<T> interactingBody = new InteractingBody<T>();
				interactingBody.interactingDirection = getIntersectionDirection(body, calculationVel, frictionBody);
				interactingBody.body = frictionBody;
				interactingBodies.add(interactingBody);

				if (frictionBody.isStaticInAxis(interactingBody.interactingDirection)) {
					//resetPos.set(interactingBody.interactingDirection, CollisionResolver.snap(body, calculationVel, interactingBody));
					//body.getVelocity().set(interactingBody.interactingDirection, 0f);
					//body.setStaticInAxis(interactingBody.interactingDirection);
				}
			}

			body.getPosition().set(resetPos);
			body.getPosition().add(body.getVelocity().multiplied(timeDelta));

			ArrayList<PhysicsBody<T>> collidingBodies = getIntersecting(body);

			for (PhysicsBody<T> collidingBody : collidingBodies) {
				InteractingBody<T> interactingBody = null;
				for (InteractingBody<T> temp : interactingBodies) {
					if (temp.equals(collidingBody)) {
						interactingBody = temp;
					}
				}

				if (interactingBody == null) {
					interactingBody = new InteractingBody<T>();
					interactingBody.body = collidingBody;
					interactingBody.interactingDirection = getIntersectionDirection(body, calculationVel, collidingBody);
				}

				//no real calculation, just an attempt to guess if the collision is just sliding over an object
				boolean isCollision = Math.abs(getGravity().get(interactingBody.interactingDirection) * timeDelta) * 2 > Math.abs(body.getVelocity().get(interactingBody.interactingDirection));

				if (isCollision) interactingBody.collision = true;
			}

			if (interactingBodies.size() > 0) CollisionResolver.resolve(body, interactingBodies, calculationVel, timeDelta);

			//stop if moving to slow
			for (int i = 0; i < body.getVelocity().getComponentCount(); i++) {
				if (Math.abs(body.getVelocity().get(i)) < NOT_MOVING_THRESHOLD) {
					body.getVelocity().set(i, 0f);
				}
			}

			//updateBody(timeDelta, body);
		}
	}

	private void updateBody(float timeDelta, PhysicsBody<T> body) {
		//body.getVelocity().add((getGravity().multiplied(timeDelta)));
		//body.getPosition().add(body.getVelocity().multiplied(timeDelta));
		/*ArrayList<PhysicsBody> intersectingBodies = getIntersecting(body);

		if (intersectingBodies.size() != 0) {
			CollisionResolver.resolve(body, intersectingBodies, timeDelta);
		}

		for (int i = 0; i < body.getVelocity().getComponentCount(); i++) {
			if (Math.abs(body.getVelocity().get(i)) < NOT_MOVING_THRESHOLD) {
				body.getVelocity().set(i, 0f);
			}
		}*/

	}

	private ArrayList<PhysicsBody<T>> getIntersecting(PhysicsBody<T> body) {
		ArrayList<PhysicsBody<T>> intersectingBodies = new ArrayList<PhysicsBody<T>>();

		for (PhysicsBody<T> staticBody : staticBodys) {
			if (body.intersects(staticBody)) {
				intersectingBodies.add(staticBody);
			}
		}

		for (PhysicsBody<T> listBody : bodies) {
			if (listBody != body && body.intersects(listBody)) {
				intersectingBodies.add(listBody);
			}
		}

		return intersectingBodies;
	}

	/*public RayIntersection getIntersecting(Ray ray) {
		ArrayList<RayIntersection> intersectingBodies = new ArrayList<RayIntersection>();

		for (PhysicsBody staticBody : staticBodys) {
			RayIntersection intersection = ray.getIntersectionPoint(staticBody);
			if (intersection != null) {
				intersectingBodies.add(intersection);
			}
		}

		for (PhysicsBody listBody : bodies) {
			RayIntersection intersection = ray.getIntersectionPoint(listBody);
			if (intersection != null) {
				intersectingBodies.add(intersection);
			}
		}

		//RayIntersection[] intersections
		//Collections.sort(intersectingBodies);
		//for (RayIntersection ri : intersectingBodies) {
		//	System.out.println(ri.intersectionPoint);
		//}

		if (intersectingBodies.size() != 0) {
			return intersectingBodies.get(0);
		} else {
			return null;
		}
	}*/

	public RayIntersection<T> getIntersecting(Ray<T> ray) {
		ArrayList<RayIntersection<T>> intersectingBodies = new ArrayList<RayIntersection<T>>();

		//TODO change to partitioned space
		for (PhysicsBody<T> staticBody : staticBodys) {
			RayIntersection<T> intersection = ray.getIntersectionPoint(staticBody);
			if (intersection != null) {
				intersectingBodies.add(intersection);
			}
		}

		for (PhysicsBody<T> listBody : bodies) {
			RayIntersection<T> intersection = ray.getIntersectionPoint(listBody);
			if (intersection != null) {
				intersectingBodies.add(intersection);
			}
		}

		//RayIntersection[] intersections
		/*Collections.sort(intersectingBodies);
		for (RayIntersection ri : intersectingBodies) {
			System.out.println(ri.intersectionPoint);
		}*/

		if (intersectingBodies.size() != 0) {
			return intersectingBodies.get(0);
		} else {
			return null;
		}
	}

	public PhysicsBody<T> getIntersecting(T point) {
		//TODO change to partitioned space
		for (PhysicsBody<T> staticBody : staticBodys) {
			if (staticBody.intersects(point)) { return staticBody; }
		}

		for (PhysicsBody<T> listBody : bodies) {
			if (listBody.intersects(point)) { return listBody; }
		}

		return null;
	}

	public int getIntersectionDirection(PhysicsBody<T> movingBody, T velocity, PhysicsBody<T> intersectedBody) {
		T intersectionVolume = velocity.newInstance();

		for (int axis = 0; axis < velocity.getComponentCount(); axis++) {
			if (velocity.get(axis) < 0) {
				intersectionVolume.set(axis, (intersectedBody.getPosition().get(axis) + intersectedBody.bounding.b.get(axis)) - (movingBody.getPosition().get(axis) + movingBody.bounding.a.get(axis)));
			} else {
				intersectionVolume.set(axis, (movingBody.getPosition().get(axis) + movingBody.bounding.b.get(axis)) - (intersectedBody.getPosition().get(axis) + intersectedBody.bounding.a.get(axis)));
			}
			if (intersectionVolume.get(axis) != 0f) intersectionVolume.set(axis, Math.abs(velocity.get(axis)) / intersectionVolume.get(axis));
		}

		return VectorTools.getGreatestComponentIndex(intersectionVolume);
	}

	private Vectorf<?> getGravity() {
		return gravity;
	}

	public void render() {
		for (PhysicsBody<T> body : staticBodys) {
			//renderAABB(body.bounding, body.getPosition(), 1f, 1f, 1f);
			renderTop(body.bounding, body.getPosition());
		}

		for (PhysicsBody<T> body : bodies) {
			//renderAABB(body.bounding, body.getPosition(), 1f, 0f, 0f);
			renderTop(body.bounding, body.getPosition());
		}

	}

	public void removeMarked() {
		for (int i = bodies.size() - 1; i >= 0; i--) {
			if (bodies.get(i).markedToRemoveFromWorld()) bodies.remove(i);
		}

		for (int i = staticBodys.size() - 1; i >= 0; i--) {
			if (staticBodys.get(i).markedToRemoveFromWorld()) staticBodys.remove(i);
		}
	}

	private void renderAABB(BoundingAABB<T> boundAABB, Vector3f offset, float r, float g, float b) {
		glColor3f(r, g, b);

		glPushMatrix();
		glTranslatef(offset.get(0), offset.get(1), offset.get(2));

		glBegin(GL_LINES);

		//top
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.a.get(2));

		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.b.get(2));

		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.b.get(2));

		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.a.get(2));

		//bottom
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.a.get(2));
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.a.get(2));

		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.a.get(2));
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.b.get(2));

		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.b.get(2));

		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.a.get(2));

		//connection

		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.a.get(2));

		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.a.get(2));

		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.b.get(2));

		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.b.get(2));

		glEnd();

		glPopMatrix();

		glColor3f(1f, 1f, 1f);
	}

	private void renderTop(BoundingAABB<T> boundAABB, T offset) {
		glPushMatrix();
		glTranslatef(offset.get(0), offset.get(1), offset.get(2));

		//glEnable(GL_TEXTURE_2D);
		if (debugTexture != null) debugTexture.bind(0);
		glBegin(GL_QUADS);

		//top
		glNormal3f(0f, 1f, 0f);
		glTexCoord2f(0f, 0f);
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.a.get(2));
		glTexCoord2f(0f, 1f);
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 1f);
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 0f);
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.a.get(2));

		//left
		glNormal3f(-1f, 0f, 0f);
		glTexCoord2f(0f, 0f);
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glTexCoord2f(0f, 1f);
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 1f);
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 0f);
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.a.get(2));

		//right
		glNormal3f(1f, 0f, 0f);
		glTexCoord2f(0f, 0f);
		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glTexCoord2f(0f, 1f);
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.a.get(2));
		glTexCoord2f(1f, 1f);
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 0f);
		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.b.get(2));

		//front
		glNormal3f(0f, 0f, 1f);
		glTexCoord2f(0f, 0f);
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glTexCoord2f(0f, 1f);
		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 1f);
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.b.get(2));
		glTexCoord2f(1f, 0f);
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.b.get(2));

		//back
		glNormal3f(0f, 0f, -1f);
		glTexCoord2f(0f, 0f);
		glVertex3f(boundAABB.a.get(0), boundAABB.a.get(1), boundAABB.a.get(2));
		glTexCoord2f(0f, 1f);
		glVertex3f(boundAABB.a.get(0), boundAABB.b.get(1), boundAABB.a.get(2));
		glTexCoord2f(1f, 1f);
		glVertex3f(boundAABB.b.get(0), boundAABB.b.get(1), boundAABB.a.get(2));
		glTexCoord2f(1f, 0f);
		glVertex3f(boundAABB.b.get(0), boundAABB.a.get(1), boundAABB.a.get(2));

		glEnd();
		//glDisable(GL_TEXTURE_2D);

		glPopMatrix();
	}

}
