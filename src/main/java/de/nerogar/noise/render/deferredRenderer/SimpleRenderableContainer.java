package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noiseInterface.math.ITransformation;
import de.nerogar.noiseInterface.render.deferredRenderer.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleRenderableContainer extends ArrayList<IRenderable> implements IRenderable {

	private ITransformation transformation;

	@Override
	public ITransformation getTransformation() {
		return transformation;
	}

	@Override
	public void setTransformation(ITransformation transformation) {
		this.transformation = transformation;
		for (IRenderable renderable : this) {
			ITransformation childTransformation = renderable.getTransformation();

			if (childTransformation != null) {
				childTransformation.setParent(transformation);
			}
		}
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
