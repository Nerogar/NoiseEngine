package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderableContainer;

public class RenderableComponent extends AbstractComponent {

	private int                  renderPassIndex;
	private IRenderableContainer renderable;

	public RenderableComponent() { }

	public RenderableComponent init(IRenderableContainer renderable, int renderPassIndex) {
		this.renderable = renderable;
		this.renderPassIndex = renderPassIndex;

		return this;
	}

	public RenderableComponent init(IRenderableContainer renderable) {
		return init(renderable, 0);
	}

	public IRenderableContainer getRenderableContainer() { return renderable; }

	public int getRenderPassIndex()                      { return renderPassIndex; }

}
