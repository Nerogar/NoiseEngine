package de.nerogar.noise.physics;

import de.nerogar.noise.util.Vectorf;

public class InteractingBody<T extends Vectorf<T>> {
	public PhysicsBody<T> body;
	public int interactingDirection;
	public boolean collision;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhysicsBody) return obj == body;
		return false;
	}
}
