package de.nerogar.noiseInterface.render.deferredRenderer;

import de.nerogar.noise.util.Color;

import java.util.List;

public interface ILight extends IRenderable {

	Color DEBUG_MESH_COLOR = new Color(0x555555);

	void renderBatch(IRenderContext renderContext, List<ILight> lights);

	@Override
	default void getLights(List<ILight> lights) {
		lights.add(this);
	}
}
