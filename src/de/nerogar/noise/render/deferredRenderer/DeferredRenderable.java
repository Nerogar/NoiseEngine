package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Matrix4f;

public class DeferredRenderable implements IRenderable {

	private DeferredContainer container;
	private Matrix4f modelMatrix;

	public DeferredRenderable(DeferredContainer container, Matrix4f modelMatrix) {
		this.container = container;
		this.modelMatrix = modelMatrix;
	}

	public DeferredContainer getContainer() {
		return container;
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public void setModelMatrix(Matrix4f modelMatrix) {
		this.modelMatrix = modelMatrix;
	}

}
