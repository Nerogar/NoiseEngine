package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.util.Color;
import de.nerogar.noiseInterface.math.IReadOnlyTransformation;

import java.util.List;

public interface ILight {

	Color DEBUG_MESH_COLOR = new Color(0x555555);

	IReadOnlyTransformation getTransformation();

	void setTransformation(IReadOnlyTransformation transformation);

	void renderBatch(IRenderContext renderContext, List<ILight> lights);

}
