package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noise.render.RenderProperties3f;

public class TransformationComponent extends AbstractComponent {

	private RenderProperties3f transformation;

	private int transformModCount;

	public TransformationComponent(float x, float y, float z, float yaw, float pitch, float roll) {
		this.transformation = new RenderProperties3f(
				yaw, pitch, roll,
				x, y, z,
				1, 1, 1
		);
	}

	public TransformationComponent(float x, float y, float z) {
		this(x, y, z, 0, 0, 0);
	}

	public TransformationComponent() {
		this(0, 0, 0, 0, 0, 0);
	}

	@Override
	public boolean hasChanged() { return transformModCount != transformation.getModCount(); }

	@Override
	public void resetChangedState() { transformModCount = transformation.getModCount(); }

	public RenderProperties3f getTransformation() { return transformation; }

	public float getX()                           { return transformation.getX(); }

	public void setX(float x)                     { transformation.setX(x); }

	public float getY()                           { return transformation.getY(); }

	public void setY(float y)                     { transformation.setY(y); }

	public float getZ()                           { return transformation.getZ(); }

	public void setZ(float z)                     { transformation.setZ(z); }

	public float getYaw()                         { return transformation.getYaw(); }

	public void setYaw(float yaw)                 { transformation.setYaw(yaw); }

	public float getPitch()                       { return transformation.getPitch(); }

	public void setPitch(float pitch)             { transformation.setPitch(pitch); }

	public float getRoll()                        { return transformation.getRoll(); }

	public void setRoll(float roll)               { transformation.setRoll(roll); }
}
