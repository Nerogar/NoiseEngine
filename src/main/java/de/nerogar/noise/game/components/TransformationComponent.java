package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noise.math.Transformation;

public class TransformationComponent extends AbstractComponent {

	private Transformation transformation;

	private int transformModCount;

	public TransformationComponent(float x, float y, float z, float yaw, float pitch, float roll) {
		this.transformation = new Transformation(
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

	public Transformation getTransformation() { return transformation; }

}
