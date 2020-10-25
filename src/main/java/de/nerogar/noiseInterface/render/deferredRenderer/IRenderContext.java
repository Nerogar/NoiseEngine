package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.render.camera.IReadOnlyCamera;

public interface IRenderContext {

	IReadOnlyCamera getCamera();

	int getgBufferWidth();

	int getgBufferHeight();

}
