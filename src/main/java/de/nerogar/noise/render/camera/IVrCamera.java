package de.nerogar.noise.render.camera;

import de.nerogar.noiseInterface.math.IMatrix4f;

public interface IVrCamera extends IMultiCamera {

	void setBaseViewMatrix(IMatrix4f baseViewMatrix);

}
