package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

public class RenderContext implements IRenderContext {

	private final IReadOnlyCamera camera;
	private final int             gBufferWidth;
	private final int             gBufferHeight;

	public RenderContext(IReadOnlyCamera camera, int gBufferWidth, int gBufferHeight) {
		this.camera = camera;
		this.gBufferWidth = gBufferWidth;
		this.gBufferHeight = gBufferHeight;
	}

	@Override
	public IReadOnlyCamera getCamera() {
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
