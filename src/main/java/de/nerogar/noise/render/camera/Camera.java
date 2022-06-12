package de.nerogar.noise.render.camera;

import de.nerogar.noiseInterface.math.ITransformation;

public abstract class Camera implements IReadOnlyCamera {

	public abstract void setAspect(float aspect);

	public abstract ITransformation getTransformation();

	public abstract void setTransformation(ITransformation transformation);

	protected abstract void setUnitRays();

	protected abstract void setProjectionMatrix();

}
