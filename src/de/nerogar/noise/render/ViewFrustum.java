package de.nerogar.noise.render;

import de.nerogar.noise.util.Vector3f;

public class ViewFrustum {

	private PerspectiveCamera camera;

	private float farWidth;
	private float farHeight;

	private float inverseFar;

	public ViewFrustum() {

	}

	public void setPlanes(PerspectiveCamera camera) {
		this.camera = camera;

		farHeight = (float) (Math.tan(Math.toRadians(camera.getFOV()) / 2.0)) * camera.getFar();
		farWidth = (float) (Math.tan(Math.toRadians(camera.getFOV()) / 2.0)) * camera.getFar() * camera.getAspect();

		inverseFar = 1.0f / camera.getFar();
	}

	public float getPointDistance(Vector3f point) {
		camera.pointToViewSpace(point);

		float near = point.getZ() - camera.getNear(); //near
		float far = -camera.getFar() - point.getZ(); //far

		float left = farWidth * point.getZ() * inverseFar - point.getX(); //left
		float right = point.getX() + farWidth * point.getZ() * inverseFar; //right

		float bottom = farHeight * point.getZ() * inverseFar - point.getY(); //bottom
		float top = point.getY() + farHeight * point.getZ() * inverseFar; //top

		float max = near;
		if (far > max) max = far;
		if (left > max) max = left;
		if (right > max) max = right;
		if (bottom > max) max = bottom;
		if (top > max) max = top;

		return max;
	}
}
