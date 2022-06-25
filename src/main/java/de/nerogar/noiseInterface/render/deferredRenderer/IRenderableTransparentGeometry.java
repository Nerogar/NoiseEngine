package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noiseInterface.math.IReadOnlyTransformation;

public interface IRenderableTransparentGeometry {

	IReadOnlyTransformation getTransformation();

	void setTransformation(IReadOnlyTransformation transformation);

	void renderTransparentGeometry(IRenderContext renderContext);

}
