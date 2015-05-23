package de.nerogar.noise.util;

public class Color {

	float[] colors;

	//private FloatBuffer colorBuffer;

	public Color() {
		this(0.0f, 0.0f, 0.0f, 1.0f);
	}

	public Color(int red, int green, int blue, int alpha) {
		this(red / 255f, blue / 255f, green / 255f, alpha / 255f);
	}

	public Color(int argb) {
		this((argb >>> 16) & 0xff, (argb >>> 8) & 0xff, (argb) & 0xff, (argb >>> 24) & 0xff);
	}

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

	public int getARGB() {
		return (((int) (colors[3] * 255) & 0xff) << 24) | (((int) (colors[0] * 255) & 0xff) << 16) | (((int) (colors[1] * 255) & 0xff) << 8) | (((int) (colors[2] * 255) & 0xff));
	}

	/*public FloatBuffer getFloatBuffer() {
		if (colorBuffer == null) {
			colorBuffer = BufferUtils.createFloatBuffer(4);
			colorBuffer.put(colors);
			colorBuffer.flip();
		}

		return colorBuffer;
	}*/
}
