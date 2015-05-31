package de.nerogar.noise.util;

public class Color {

	float[] colors;

	/**Creates a new black color.*/
	public Color() {
		this(0.0f, 0.0f, 0.0f, 1.0f);
	}

	/**
	 * Creates a new color from integer components.
	 * Each component should be i the range [0, 255],
	 * where 0 is mapped to 0.0 and 255 is mapped to 1.0
	 * 
	 * @param red red component
	 * @param green green component
	 * @param blue blue component
	 * @param alpha alpha component
	 */
	public Color(int red, int green, int blue, int alpha) {
		this(red / 255f, blue / 255f, green / 255f, alpha / 255f);
	}

	/**
	 * Creates a new color from a single integer.
	 * Each component is represented by 1 byte in the order: alpha, red, green, blue.
	 * 
	 * @param argb all components in a single integer
	 */
	public Color(int argb) {
		this((argb >>> 16) & 0xff, (argb >>> 8) & 0xff, (argb) & 0xff, (argb >>> 24) & 0xff);
	}

	/**
	 * Creates a new color from float components.
	 * Each component should be i the range [0.0, 1.0]
	 * 
	 * @param red red component
	 * @param green green component
	 * @param blue blue component
	 * @param alpha alpha component
	 */
	public Color(float red, float green, float blue, float alpha) {
		colors = new float[] { red, green, blue, alpha };
	}

	public float getR() {
		return colors[0];
	}

	public float getG() {
		return colors[1];
	}

	public float getB() {
		return colors[2];
	}

	public float getA() {
		return colors[3];
	}

	/**
	 * Creates an integer containing all components in a single integer.
	 * Each component is represented by 1 byte in the order: alpha, red, green, blue.
	 * 
	 * @return The argb value
	 */
	public int getARGB() {
		return (((int) (colors[3] * 255) & 0xff) << 24) | (((int) (colors[0] * 255) & 0xff) << 16) | (((int) (colors[1] * 255) & 0xff) << 8) | (((int) (colors[2] * 255) & 0xff));
	}
}
