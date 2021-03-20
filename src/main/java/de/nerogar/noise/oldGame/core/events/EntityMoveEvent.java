package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.Entity;

public class EntityMoveEvent implements IEvent {

	private Entity entity;

	private float oldX, oldY, oldZ, oldRotation, oldScale;
	private float newX, newY, newZ, newRotation, newScale;

	public EntityMoveEvent(Entity entity, float oldX, float oldY, float oldZ, float oldRotation, float oldScale, float newX, float newY, float newZ, float newRotation, float newScale) {
		this.entity = entity;

		this.oldX = oldX;
		this.oldY = oldY;
		this.oldZ = oldZ;
		this.oldRotation = oldRotation;
		this.oldScale = oldScale;

		this.newX = newX;
		this.newY = newY;
		this.newZ = newZ;
		this.newRotation = newRotation;
		this.newScale = newScale;
	}

	public Entity getEntity()     { return entity; }

	public float getOldX()        { return oldX; }

	public float getOldY()        { return oldY; }

	public float getOldZ()        { return oldZ; }

	public float getOldRotation() { return oldRotation; }

	public float getOldScale()    { return oldScale; }

	public float getNewX()        { return newX; }

	public float getNewY()        { return newY; }

	public float getNewZ()        { return newZ; }

	public float getNewRotation() { return newRotation; }

	public float getNewScale()    { return newScale; }

}
