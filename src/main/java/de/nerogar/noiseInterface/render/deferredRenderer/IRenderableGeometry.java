package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noiseInterface.math.IReadOnlyTransformation;
import de.nerogar.noiseInterface.render.deferredRenderer.enums.DepthTestMode;
import de.nerogar.noiseInterface.render.deferredRenderer.enums.FaceCullMode;

public interface IRenderableGeometry {

	IReadOnlyTransformation getTransformation();

	default FaceCullMode getFaceCullMode()   {return FaceCullMode.BACK;}

	default DepthTestMode getDepthTestMode() {return DepthTestMode.READ_AND_WRITE;}

	void setTransformation(IReadOnlyTransformation transformation);

	void renderGeometry(IRenderContext renderContext);

}
