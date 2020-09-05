package de.nerogar.noise.render;

import de.nerogar.noise.render.camera.Camera;
import de.nerogar.noiseInterface.math.IVector3f;

public interface IViewRegion {

	void setPlanes(Camera camera);

	float getPointDistance(IVector3f point);

}
