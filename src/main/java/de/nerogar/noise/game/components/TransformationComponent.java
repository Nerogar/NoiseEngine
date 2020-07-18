package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;

public class TransformationComponent extends AbstractComponent {

	private float x, y, z;
	private float yaw, pitch, roll;

	public TransformationComponent(float x, float y, float z, float yaw, float pitch, float roll) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	public TransformationComponent(float x, float y, float z) {
		this(x, y, z, 0, 0, 0);
	}

	public TransformationComponent() {
		this(0, 0, 0, 0, 0, 0);
	}

	public float getX()               { return x; }

	public void setX(float x)         { this.x = x; }

	public float getY()               { return y; }

	public void setY(float y)         { this.y = y; }

	public float getZ()               { return z; }

	public void setZ(float z)         { this.z = z; }

	public float getYaw()             { return yaw; }

	public void setYaw(float yaw)     { this.yaw = yaw; }

	public float getPitch()           { return pitch; }

	public void setPitch(float pitch) { this.pitch = pitch; }

	public float getRoll()            { return roll; }

	public void setRoll(float roll)   { this.roll = roll; }
}
