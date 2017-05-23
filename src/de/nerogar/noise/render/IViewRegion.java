package de.nerogar.noise.render;

import de.nerogar.noise.util.Bounding;
import de.nerogar.noise.util.Vector3f;

public interface IViewRegion {

	public void setPlanes(Camera camera);

	public float getPointDistance(Vector3f point);

	public Bounding getBounding();

}
