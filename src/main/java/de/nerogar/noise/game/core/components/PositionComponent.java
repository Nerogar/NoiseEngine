package de.nerogar.noise.game.core.components;

import de.nerogar.noise.game.Component;
import de.nerogar.noise.game.core.events.EntityMoveEvent;
import de.nerogar.noise.game.core.systems.PositionLookupSystem;

public class PositionComponent extends Component<PositionComponent> {

	private float x;
	private float y;
	private float z;
	private float rotation;
	private float scale;

	public PositionComponent() {
	}

	public PositionComponent(float x, float y, float z) {
		this(x, y, z, 0, 1);
	}

	public PositionComponent(float x, float y, float z, float rotation, float scale) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.scale = scale;
	}

	public float getX()                                { return x; }

	public float getY()                                { return y; }

	public float getZ()                                { return z; }

	public void setPosition(float x, float y, float z) { setPosition(x, y, z, rotation, scale); }

	public float getRotation()                         { return rotation; }

	public void setRotation(float rotation)            { setPosition(x, y, z, rotation, scale); }

	public float getScale()                            { return scale; }

	public void setScale(float scale)                  { setPosition(x, y, z, rotation, scale); }

	public void setPosition(float x, float y, float z, float rotation, float scale) {
		float oldX = this.x;
		float oldY = this.y;
		float oldZ = this.z;
		float oldRotation = this.rotation;
		float oldScale = this.scale;

		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.scale = scale;

		getEntity().getMap().getSystem(PositionLookupSystem.class).updateEntity(getEntity());

		getEntity().getMap().getEventManager().trigger(new EntityMoveEvent(getEntity(), oldX, oldY, oldZ, oldRotation, oldScale, x, y, z, rotation, scale));
	}

	@Override
	public PositionComponent newInstance() {
		return new PositionComponent();
	}

	@Override
	public void copyFrom(PositionComponent other) {
		x = other.x;
		y = other.y;
		z = other.z;
		rotation = other.rotation;
		scale = other.scale;
	}

}
