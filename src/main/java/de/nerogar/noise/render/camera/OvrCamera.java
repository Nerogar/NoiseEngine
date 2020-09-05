package de.nerogar.noise.render.camera;

import de.nerogar.noise.render.vr.OvrContext;
import de.nerogar.noiseInterface.math.IMatrix4f;

public class OvrCamera implements IVrCamera {

	private OvrContext      ovrContext;
	private ManagedCamera[] eyes;

	private IMatrix4f baseViewMatrix;

	public OvrCamera(OvrContext ovrContext) {
		this.ovrContext = ovrContext;

		eyes = new ManagedCamera[] {
				new ManagedCamera(),
				new ManagedCamera()
		};
	}

	public IMatrix4f getBaseViewMatrix() {
		return baseViewMatrix;
	}

	@Override
	public void setBaseViewMatrix(IMatrix4f baseViewMatrix) {
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
