package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

public class RenderableComponent extends AbstractComponent {

	private IRenderable renderable;

	public RenderableComponent(IRenderable renderable) {
		this.renderable = renderable;
	}

	public IRenderable getRenderable() { return renderable; }
}
