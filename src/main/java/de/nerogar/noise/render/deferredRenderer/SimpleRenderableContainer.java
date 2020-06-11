package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noiseInterface.render.deferredRenderer.ILight;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

import java.util.ArrayList;
import java.util.List;

public class SimpleRenderableContainer extends ArrayList<IRenderable> implements IRenderable {

	private RenderProperties3f renderProperties;

	public SimpleRenderableContainer() {
		renderProperties = new RenderProperties3f();
	}

	@Override
	public RenderProperties3f getRenderProperties() {
		return renderProperties;
	}

	@Override
	public void setParentRenderProperties(RenderProperties3f parentRenderProperties) {
		renderProperties.setParent(parentRenderProperties);
	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		for (IRenderable renderable : this) {
			renderable.renderGeometry(renderContext);
		}
	}

	@Override
	public void getLights(List<ILight> lights) {
		for (IRenderable renderable : this) {
			renderable.getLights(lights);
		}
	}

}
