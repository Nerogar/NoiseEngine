package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.Mesh;
import de.nerogar.noise.render.Texture2D;

public class DeferredContainer {
	private Mesh mesh;

	private Texture2D colorTexture;
	private Texture2D normalTexture;
	private Texture2D lightTexture;

	public DeferredContainer(Mesh mesh, Texture2D colorTexture, Texture2D normalTexture, Texture2D lightTexture) {
		this.mesh = mesh;
		this.colorTexture = colorTexture;
		this.normalTexture = normalTexture;
		this.lightTexture = lightTexture;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public Texture2D getColorTexture() {
		return colorTexture;
	}

	public Texture2D getNormalTexture() {
		return normalTexture;
	}

	public Texture2D getLightTexture() {
		return lightTexture;
	}

}
