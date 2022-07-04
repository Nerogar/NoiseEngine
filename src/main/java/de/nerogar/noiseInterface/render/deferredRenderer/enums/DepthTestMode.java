package de.nerogar.noiseInterface.render.deferredRenderer.enums;

import static org.lwjgl.opengl.GL11.*;

public enum DepthTestMode {
	NONE(false, false),
	READ(true, false),
	WRITE(false, true), // TODO: might have to change the depthFunc to GL_ALWAYS here
	READ_AND_WRITE(true, true),
	;

	private final boolean isDepthTestActive;
	private final boolean writeDepthValue;

	DepthTestMode(boolean isDepthTestActive, boolean writeDepthValue) {
		this.isDepthTestActive = isDepthTestActive;
		this.writeDepthValue = writeDepthValue;
	}

	/**
	 * Changes openGL state to reflect the new depth test mode
	 *
	 * @param previous the previously active mode
	 * @return the new active mode
	 */
	public DepthTestMode activate(DepthTestMode previous) {
		if (previous == null || (this.isDepthTestActive != previous.isDepthTestActive)) {
			if (this.isDepthTestActive) {
				glEnable(GL_DEPTH_TEST);
			} else {
				glDisable(GL_DEPTH_TEST);
			}
		}

		if (previous == null || (this.writeDepthValue != previous.writeDepthValue)) {
			glDepthMask(this.writeDepthValue);
		}

		return this;
	}

}
