package de.nerogar.noise.render;

import de.nerogar.noise.util.Matrix4f;

public interface RenderProperties {

	public Matrix4f getModelMatrix();

	public boolean isVisible();
	
	public void setVisible(boolean visible);
}
