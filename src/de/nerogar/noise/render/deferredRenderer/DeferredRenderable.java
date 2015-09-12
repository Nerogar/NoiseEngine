package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.IRenderable;
import de.nerogar.noise.render.RenderProperties3f;

/**
 * an object that can be added to the {@link DeferredRenderable DeferredRenderer}
 */
public class DeferredRenderable implements IRenderable {

	private DeferredContainer container;
	private RenderProperties3f renderProperties;

	/**
	 * @param container the {@link DeferredContainer DeferredContainer} describing the appearance
	 * @param renderProperties the {@link RenderProperties3f RenderProperties3f} describing properties like position and rotation
	 */
	public DeferredRenderable(DeferredContainer container, RenderProperties3f renderProperties) {
		this.container = container;
		this.renderProperties = renderProperties;
	}

	/**
	 * @return the {@link DeferredContainer DeferredContainer}
	 */
	public DeferredContainer getContainer() {
		return container;
	}

	/**
	 * @return the {@link RenderProperties3f RenderProperties3f}
	 */
	public RenderProperties3f getRenderProperties() {
		return renderProperties;
	}

}
