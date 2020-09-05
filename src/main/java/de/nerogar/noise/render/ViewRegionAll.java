package de.nerogar.noise.render;

import de.nerogar.noise.render.camera.Camera;
import de.nerogar.noiseInterface.math.IVector3f;

public class ViewRegionAll implements IViewRegion {

	public ViewRegionAll() {
	}

	@Override
	public void setPlanes(Camera cam) {
	}

	public float getPointDistance(IVector3f point) {
		return -1;
	}

}
