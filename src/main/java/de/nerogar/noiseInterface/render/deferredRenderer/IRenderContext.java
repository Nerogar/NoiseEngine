package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.camera.IReadOnlyCamera;

public interface IRenderContext {

	IReadOnlyCamera getCamera();

	int getGBufferWidth();

	int getGBufferHeight();

	Texture2D getDepthTexture();

	Texture2D getAlbedoTexture();

	Texture2D getNormalTexture();

	Texture2D getMaterialTexture();

}
