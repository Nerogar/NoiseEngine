package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.camera.IReadOnlyCamera;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

public class RenderContext implements IRenderContext {

	private final IReadOnlyCamera camera;
	private final int             gBufferWidth;
	private final int             gBufferHeight;

	private final Texture2D gBufferDepthTexture;
	private final Texture2D gBufferAlbedoTexture;
	private final Texture2D gBufferNormalTexture;
	private final Texture2D gBufferMaterialTexture;
	private final Texture2D gBufferLightsTexture;
	private final Texture2D lBufferLightsTexture;

	public RenderContext(
			IReadOnlyCamera camera, int gBufferWidth, int gBufferHeight,
			Texture2D gBufferDepthTexture, Texture2D gBufferAlbedoTexture, Texture2D gBufferNormalTexture, Texture2D gBufferMaterialTexture, Texture2D gBufferLightsTexture,
			Texture2D lBufferLightsTexture) {

		this.camera = camera;
		this.gBufferWidth = gBufferWidth;
		this.gBufferHeight = gBufferHeight;

		this.gBufferDepthTexture = gBufferDepthTexture;
		this.gBufferAlbedoTexture = gBufferAlbedoTexture;
		this.gBufferNormalTexture = gBufferNormalTexture;
		this.gBufferMaterialTexture = gBufferMaterialTexture;
		this.gBufferLightsTexture = gBufferLightsTexture;

		this.lBufferLightsTexture = lBufferLightsTexture;
	}

	@Override
	public IReadOnlyCamera getCamera() {
		return camera;
	}

	@Override
	public int getBufferWidth() {
		return gBufferWidth;
	}

	@Override
	public int getBufferHeight() {
		return gBufferHeight;
	}

	@Override
	public Texture2D getGBufferDepthTexture() {
		return gBufferDepthTexture;
	}

	@Override
	public Texture2D getGBufferAlbedoTexture() {
		return gBufferAlbedoTexture;
	}

	@Override
	public Texture2D getGBufferNormalTexture() {
		return gBufferNormalTexture;
	}

	@Override
	public Texture2D getGBufferMaterialTexture() {
		return gBufferMaterialTexture;
	}

	@Override
	public Texture2D getGBufferLightsTexture() {
		return gBufferLightsTexture;
	}

	@Override
	public Texture2D getLBufferLightsTexture() {
		return lBufferLightsTexture;
	}
}
