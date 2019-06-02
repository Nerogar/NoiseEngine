package de.nerogar.noise.render;

import de.nerogar.noise.render.camera.Camera;
import de.nerogar.noise.util.Bounding;
import de.nerogar.noise.util.Vector3f;

public class ViewRegionAll implements IViewRegion {

	public ViewRegionAll() {
	}

	@Override
	public void setPlanes(Camera cam) {
	}

	public float getPointDistance(Vector3f point) {
		return -1;
	}

	@Override
	public Bounding getBounding() {
		return null;
	}

}
