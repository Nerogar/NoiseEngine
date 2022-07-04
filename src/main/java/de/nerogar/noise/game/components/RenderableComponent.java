package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderableContainer;

public class RenderableComponent extends AbstractComponent {

	private int                  renderPassIndex;
	private IRenderableContainer renderable;

	public RenderableComponent(IRenderableContainer renderable, int renderPassIndex) {
		this.renderable = renderable;
		this.renderPassIndex = renderPassIndex;
	}

	public RenderableComponent(IRenderableContainer renderable) {
		this(renderable, 0);
	}

	public IRenderableContainer getRenderableContainer() {return renderable;}

	public int getRenderPassIndex() {
		return renderPassIndex;
	}

}
