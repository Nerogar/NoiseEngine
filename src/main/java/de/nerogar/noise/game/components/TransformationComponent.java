package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noise.math.Transformation;
import de.nerogar.noiseInterface.math.ITransformation;

public class TransformationComponent extends AbstractComponent {

	public ITransformation transformation;

	public TransformationComponent() { }

	public TransformationComponent init(ITransformation transformation) {
		this.transformation = transformation;

		return this;
	}

	public TransformationComponent init(float x, float y, float z, float yaw, float pitch, float roll) {
		return init(new Transformation(
				yaw, pitch, roll,
				x, y, z,
				1, 1, 1
		));
	}

	public TransformationComponent init(float x, float y, float z) {
		return init(x, y, z, 0, 0, 0);
	}

}
