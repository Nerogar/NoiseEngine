package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.camera.IReadOnlyCamera;

public interface IRenderContext {

	IReadOnlyCamera getCamera();

	int getBufferWidth();

	int getBufferHeight();

	Texture2D getGBufferDepthTexture();

	Texture2D getGBufferAlbedoTexture();

	Texture2D getGBufferNormalTexture();

	Texture2D getGBufferMaterialTexture();

	Texture2D getGBufferLightsTexture();

	Texture2D getLBufferLightsTexture();

}
