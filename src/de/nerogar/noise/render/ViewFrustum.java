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

		float[] distances = new float[6];

		distances[0] = point.getZ() - camera.getNear(); //near
		distances[1] = -camera.getFar() - point.getZ(); //far

		distances[2] = farWidth * point.getZ() * inverseFar - point.getX(); //left
		distances[3] = point.getX() + farWidth * point.getZ() * inverseFar; //right

		distances[4] = farHeight * point.getZ() * inverseFar - point.getY(); //bottom
		distances[5] = point.getY() + farHeight * point.getZ() * inverseFar; //top

		float max = distances[0];
		for (int i = 1; i < 6; i++) {
			if (distances[i] > max) max = distances[i];
		}

		return max;
	}
}
