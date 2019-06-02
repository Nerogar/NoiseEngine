package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.vr.OvrContext;
import de.nerogar.noise.util.Matrix4f;

public class OvrCamera implements IVrCamera {

	private OvrContext      ovrContext;
	private ManagedCamera[] eyes;

	private Matrix4f baseViewMatrix;

	public OvrCamera(OvrContext ovrContext) {
		this.ovrContext = ovrContext;

		eyes = new ManagedCamera[] {
				new ManagedCamera(),
				new ManagedCamera()
		};
	}

	public Matrix4f getBaseViewMatrix() {
		return baseViewMatrix;
	}

	@Override
	public void setBaseViewMatrix(Matrix4f baseViewMatrix) {
		this.baseViewMatrix = baseViewMatrix;
	}

	public ManagedCamera[] getEyes() {
		return eyes;
	}

	@Override
	public IReadOnlyCamera[] cameras() {
		return eyes;
	}

}
