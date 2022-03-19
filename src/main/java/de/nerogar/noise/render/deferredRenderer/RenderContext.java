package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

public class RenderContext implements IRenderContext {

	private final IReadOnlyCamera camera;
	private final int             gBufferWidth;
	private final int             gBufferHeight;

	private final Texture2D depthTexture;
	private final Texture2D albedoTexture;
	private final Texture2D normalTexture;
	private final Texture2D materialTexture;

	public RenderContext(
			IReadOnlyCamera camera, int gBufferWidth, int gBufferHeight,
			Texture2D depthTexture, Texture2D albedoTexture, Texture2D normalTexture, Texture2D materialTexture
	                    ) {
		this.camera = camera;
		this.gBufferWidth = gBufferWidth;
		this.gBufferHeight = gBufferHeight;
		this.depthTexture = depthTexture;
		this.albedoTexture = albedoTexture;
		this.normalTexture = normalTexture;
		this.materialTexture = materialTexture;
	}

	@Override
	public IReadOnlyCamera getCamera() {
		return camera;
	}

	@Override
	public int getGBufferWidth() {
		return gBufferWidth;
	}

	@Override
	public int getGBufferHeight() {
		return gBufferHeight;
	}

	@Override
	public Texture2D getDepthTexture() {
		return depthTexture;
	}

	@Override
	public Texture2D getAlbedoTexture() {
		return albedoTexture;
	}

	@Override
	public Texture2D getNormalTexture() {
		return normalTexture;
	}

	@Override
	public Texture2D getMaterialTexture() {
		return materialTexture;
	}
}
