package de.nerogar.noise.render;

import de.nerogar.noise.render.camera.Camera;
import de.nerogar.noise.render.camera.PerspectiveCamera;
import de.nerogar.noise.util.Bounding;
import de.nerogar.noise.util.BoundingHexahedron;
import de.nerogar.noise.util.Ray;
import de.nerogar.noise.util.Vector3f;

public class ViewFrustum implements IViewRegion {

	private PerspectiveCamera camera;

	private float halfFarWidth;
	private float halfFarHeight;

	private float leftRightFactor;
	private float topBottomFactor;

	private float inverseFar;

	private BoundingHexahedron bounding;

	public ViewFrustum() {
		bounding = new BoundingHexahedron(new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f());
	}

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

		setBounding(camera);

	}

	private void setBounding(PerspectiveCamera camera) {
		// setup all corners
		Ray topLeft = camera.unproject(-1, 1);
		Ray topRight = camera.unproject(1, 1);
		Ray bottomLeft = camera.unproject(-1, -1);
		Ray bottomRight = camera.unproject(1, -1);

		// calculate all edges of the view region

		bounding.setPoints(

				topLeft.getStart().clone().add(topLeft.getDir().multiplied(camera.getNear())),
				topRight.getStart().clone().add(topRight.getDir().multiplied(camera.getNear())),
				bottomLeft.getStart().clone().add(bottomLeft.getDir().multiplied(camera.getNear())),
				bottomRight.getStart().clone().add(bottomRight.getDir().multiplied(camera.getNear())),

				topLeft.getStart().clone().add(topLeft.getDir().multiplied(camera.getFar())),
				topRight.getStart().clone().add(topRight.getDir().multiplied(camera.getFar())),
				bottomLeft.getStart().clone().add(bottomLeft.getDir().multiplied(camera.getFar())),
				bottomRight.getStart().clone().add(bottomRight.getDir().multiplied(camera.getFar()))

		                  );

	}

	public float getPointDistance(Vector3f point) {
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

	@Override
	public Bounding getBounding() {
		return bounding;
	}

}
