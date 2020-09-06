package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.math.Transformation;
import de.nerogar.noiseInterface.render.deferredRenderer.ILight;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

import java.util.ArrayList;
import java.util.List;

public class SimpleRenderableContainer extends ArrayList<IRenderable> implements IRenderable {

	private Transformation renderProperties;

	public SimpleRenderableContainer() {
		renderProperties = new Transformation();
	}

	@Override
	public Transformation getRenderProperties() {
		return renderProperties;
	}

	@Override
	public void setParentRenderProperties(Transformation parentRenderProperties) {
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
