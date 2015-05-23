package de.nerogar.noise.util;

public class MathHelper {
	/*private static final int LOOKUP_TABLE_LENGTH = 4096;
	private static float[] sinTable;

	private static final float HALF_PI = (float) (Math.PI * 0.5);
	public static final float PI = (float) (Math.PI);
	public static final float TAU = (float) (Math.PI * 2);

	public static float sin(float radiant) {
		float x = (radiant % TAU);
		if (x < 0) x += TAU;
		return sinTable[(int) (x * LOOKUP_TABLE_LENGTH)];
	}

	public static float cos(float radiant) {
		return sin(radiant + HALF_PI);
	}

	static {
		sinTable = new float[LOOKUP_TABLE_LENGTH];

		for (int i = 0; i < LOOKUP_TABLE_LENGTH; i++) {
			sinTable[i] = (float) Math.sin((TAU) * (float) (i / LOOKUP_TABLE_LENGTH));
		}
	}*/

	public static float clamp(float val, float min, float max) {
		return Math.min(Math.max(min, val), max);
	}

	public static int clamp(int val, int min, int max) {
		return Math.min(Math.max(min, val), max);
	}

}
