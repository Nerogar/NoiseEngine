package de.nerogar.noise.render;

import de.nerogar.noise.render.camera.Camera;
import de.nerogar.noise.render.camera.PerspectiveCamera;
import de.nerogar.noiseInterface.math.IVector3f;

public class ViewFrustum implements IViewRegion {

	private PerspectiveCamera camera;

	private float halfFarWidth;
	private float halfFarHeight;

	private float leftRightFactor;
	private float topBottomFactor;

	private float inverseFar;

	@Override
	public void setPlanes(Camera cam) {
		if (!(cam instanceof PerspectiveCamera)) throw new RuntimeException("Invalid camera type, PerspectiveCamera expected");
		this.camera = (PerspectiveCamera) cam;

		halfFarWidth = (float) (Math.tan(Math.toRadians(camera.getFOV()) / 2.0)) * camera.getFar() * camera.getAspect();
		halfFarHeight = (float) (Math.tan(Math.toRadians(camera.getFOV()) / 2.0)) * camera.getFar();

		float fovSides = (float) Math.atan(Math.tan(Math.toRadians(camera.getFOV() / 2.0)) * camera.getAspect());

		leftRightFactor = (float) (Math.cos(fovSides));
		topBottomFactor = (float) (Math.cos(Math.toRadians(camera.getFOV() / 2.0)));

		inverseFar = 1.0f / camera.getFar();
	}

	public float getPointDistance(IVector3f point) {
		float x = point.getX();
		float y = point.getY();
		float z = point.getZ();

		camera.pointToViewSpace(point);

		float near = point.getZ() + camera.getNear();
		float far = -camera.getFar() - point.getZ();

		float left = leftRightFactor * (halfFarWidth * point.getZ() * inverseFar - point.getX());
		float right = leftRightFactor * (halfFarWidth * point.getZ() * inverseFar + point.getX());

		float bottom = topBottomFactor * (halfFarHeight * point.getZ() * inverseFar - point.getY());
		float top = topBottomFactor * (halfFarHeight * point.getZ() * inverseFar + point.getY());

		float max = near;
		if (far > max) max = far;
		if (left > max) max = left;
		if (right > max) max = right;
		if (bottom > max) max = bottom;
		if (top > max) max = top;

		point.set(x, y, z);

		return max;
	}

}
