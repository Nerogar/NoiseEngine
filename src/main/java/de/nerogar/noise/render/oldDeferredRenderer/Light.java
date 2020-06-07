package de.nerogar.noise.render.oldDeferredRenderer;

import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Vector3f;

/**
 * a light used in the {@link DeferredRenderer DeferredRenderer}
 */
public class Light {

	/**position of the light*/
	public Vector3f position;

	/**color of the light*/
	public Color color;

	/**reach of the light*/
	public float reach;
	
	/**intensity of the light*/
	public float intensity;

	/**
	 * @param position initial position of the light
	 * @param color initial color of the light
	 * @param reach initial reach of the light
	 * @param intensity initial intensity of the light
	 */
	public Light(Vector3f position, Color color, float reach, float intensity) {
		this.position = position;
		this.color = color;
		this.reach = reach;
		this.intensity = intensity;
	}

}
