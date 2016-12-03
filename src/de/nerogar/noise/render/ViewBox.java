package de.nerogar.noise.render;

import de.nerogar.noise.util.Vector3f;

public class ViewBox implements IViewRegion {

	private OrthographicCamera camera;

	private float leftPlane;
	private float rightPlane;

	private float bottomPlane;
	private float topPlane;

	private float nearPlane;
	private float farPlane;

	@Override
	public void setPlanes(Camera cam) {
		if (!(cam instanceof OrthographicCamera)) throw new RuntimeException("Invalid camera type, OrthographicCamera expected");
		this.camera = (OrthographicCamera) cam;

		leftPlane = -camera.getHeight() * camera.getAspect() / 2;
		rightPlane = -leftPlane;

		bottomPlane = -camera.getHeight() / 2;
		topPlane = -bottomPlane;

		nearPlane = camera.getNear();
		farPlane = camera.getFar();
	}

	public float getPointDistance(Vector3f point) {
		float x = point.getX();
		float y = point.getY();
		float z = point.getZ();

		camera.pointToViewSpace(point);

		float left = leftPlane - point.getX();
		float right = point.getX() - rightPlane;

		float bottom = bottomPlane - point.getY();
		float top = point.getY() - topPlane;

		float near = point.getZ() - nearPlane;
		float far = farPlane - point.getZ();

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
