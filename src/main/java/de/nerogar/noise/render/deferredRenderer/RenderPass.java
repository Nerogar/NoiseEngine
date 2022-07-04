package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noiseInterface.render.deferredRenderer.IRenderPass;

public class RenderPass implements IRenderPass {

	private final SimpleRenderableContainer container;

	public RenderPass() {
		this.container = new SimpleRenderableContainer();
	}

	public SimpleRenderableContainer getContainer() {return container;}

}
