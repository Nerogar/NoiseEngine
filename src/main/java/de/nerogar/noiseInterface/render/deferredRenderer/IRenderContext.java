package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.render.camera.Camera;

public interface IRenderContext {

	public Camera getCamera();

	public int getgBufferWidth();

	public int getgBufferHeight();

}
