package de.nerogar.noise.game.components;

import de.nerogar.noise.game.AbstractComponent;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderableContainer;

public class RenderableComponent extends AbstractComponent {

	private IRenderableContainer renderable;

	public RenderableComponent(IRenderableContainer renderable) {
		this.renderable = renderable;
	}

	public IRenderableContainer getRenderableContainer() {return renderable;}
}
