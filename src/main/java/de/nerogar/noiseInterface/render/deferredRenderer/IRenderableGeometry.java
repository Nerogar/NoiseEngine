package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noiseInterface.math.IReadOnlyTransformation;

public interface IRenderableGeometry {

	IReadOnlyTransformation getTransformation();

	void setTransformation(IReadOnlyTransformation transformation);

	void renderGeometry(IRenderContext renderContext);

}
