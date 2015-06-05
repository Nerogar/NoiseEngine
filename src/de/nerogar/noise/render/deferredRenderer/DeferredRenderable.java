package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.IRenderable;
import de.nerogar.noise.render.RenderProperties3f;

public class DeferredRenderable implements IRenderable {

	private DeferredContainer container;
	private RenderProperties3f renderProperties;

	public DeferredRenderable(DeferredContainer container, RenderProperties3f renderProperties) {
		this.container = container;
		this.renderProperties = renderProperties;
	}

	public DeferredContainer getContainer() {
		return container;
	}

	public RenderProperties3f getRenderProperties() {
		return renderProperties;
	}

}
