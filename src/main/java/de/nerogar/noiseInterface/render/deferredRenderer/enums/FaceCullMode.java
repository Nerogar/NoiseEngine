package de.nerogar.noiseInterface.render.deferredRenderer.enums;

import static org.lwjgl.opengl.GL11.*;

public enum FaceCullMode {
	NONE(false, -1),
	FRONT(true, GL_FRONT),
	BACK(true, GL_BACK),
	FRONT_AND_BACK(true, GL_FRONT_AND_BACK),
	;

	private final boolean isCullingActive;
	private final int     cullingMode;

	FaceCullMode(boolean isCullActive, int cullingMode) {
		this.isCullingActive = isCullActive;
		this.cullingMode = cullingMode;
	}

	/**
	 * Changes openGL state to reflect the new face cull mode
	 *
	 * @param previous the previously active mode
	 * @return the new active mode
	 */
	public FaceCullMode activate(FaceCullMode previous) {
		if (previous == null || (this.isCullingActive != previous.isCullingActive)) {
			if (this.isCullingActive) {
				glEnable(GL_CULL_FACE);
			} else {
				glDisable(GL_CULL_FACE);
			}
		}

		if (this.isCullingActive && (previous == null || this.cullingMode != previous.cullingMode)) {
			glCullFace(this.cullingMode);
		}

		return this;
	}

}
