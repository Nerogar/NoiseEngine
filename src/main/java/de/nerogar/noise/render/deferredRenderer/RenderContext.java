package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.camera.Camera;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

public class RenderContext implements IRenderContext {

	private final Camera camera;
	private final int    gBufferWidth;
	private final int    gBufferHeight;

	public RenderContext(Camera camera, int gBufferWidth, int gBufferHeight) {
		this.camera = camera;
		this.gBufferWidth = gBufferWidth;
		this.gBufferHeight = gBufferHeight;
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public int getgBufferWidth() {
		return gBufferWidth;
	}

	@Override
	public int getgBufferHeight() {
		return gBufferHeight;
	}
}
