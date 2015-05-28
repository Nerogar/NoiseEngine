package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4f;

public interface RenderProperties {

	public void transformGL();

	public Matrix4f getModelMatrix();

}
