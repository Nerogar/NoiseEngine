package de.nerogar.noiseInterface.render.deferredRenderer.enums;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public enum BlendMode {
	NONE(false, -1, -1, -1, -1),
	ADD(true, GL_ONE, GL_ONE, GL_ONE, GL_ONE),
	OVERLAY(true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE),
	;

	private final boolean isBLendActive;
	private final int     sourceFactorRGB;
	private final int     destinationFactorRGB;
	private final int     sourceFactorAlpha;
	private final int     destinationFactorAlpha;

	BlendMode(boolean isBLendActive, int sourceFactorRGB, int destinationFactorRGB, int sourceFactorAlpha, int destinationFactorAlpha) {
		this.isBLendActive = isBLendActive;
		this.sourceFactorRGB = sourceFactorRGB;
		this.destinationFactorRGB = destinationFactorRGB;
		this.sourceFactorAlpha = sourceFactorAlpha;
		this.destinationFactorAlpha = destinationFactorAlpha;
	}

	/**
	 * Changes openGL state to reflect the new blend mode
	 *
	 * @param previous the previously active mode
	 * @return the new active mode
	 */
	public BlendMode activate(BlendMode previous) {
		if (previous == null || (this.isBLendActive != previous.isBLendActive)) {
			if (this.isBLendActive) {
				glEnable(GL_BLEND);
			} else {
				glDisable(GL_BLEND);
			}
		}

		if (this.isBLendActive && (previous == null ||
				this.sourceFactorRGB != previous.sourceFactorRGB || this.destinationFactorRGB != previous.destinationFactorRGB ||
				this.sourceFactorAlpha != previous.sourceFactorAlpha || this.destinationFactorAlpha != previous.destinationFactorAlpha)) {

			glBlendFuncSeparate(this.sourceFactorRGB, this.destinationFactorRGB, this.sourceFactorAlpha, this.destinationFactorAlpha);
		}

		return this;
	}

}
