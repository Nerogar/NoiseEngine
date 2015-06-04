package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Vector3f;

public class Light {

	public Vector3f position;
	public Color color;
	public float reach;
	public float intensity;

	private boolean dead;

	public Light(Vector3f position, Color color, float reach, float intensity) {
		this.position = position;
		this.color = color;
		this.reach = reach;
		this.intensity = intensity;
	}

	public void kill() {
		dead = true;
	}

	public boolean dead() {
		return dead;
	}

}
