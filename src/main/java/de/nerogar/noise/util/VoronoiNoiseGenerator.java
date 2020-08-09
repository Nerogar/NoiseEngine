package de.nerogar.noise.util;

import de.nerogar.noiseInterface.util.INoiseGenerator;

import java.util.Random;

public class VoronoiNoiseGenerator implements INoiseGenerator {

	private long seed;

	private int seedX;
	private int seedY;
	private int seedZ;
	private int seedW;

	private int[] randDataX;
	private int[] randDataY;

	public VoronoiNoiseGenerator(long seed) {
		setSeed(seed);
	}

	public void setSeed(long seed) {
		this.seed = seed;

		int low = (int) (seed & 0xffffffffL);
		int high = (int) ((seed >> 32) & 0xffffffffL);

		seedX = Integer.rotateRight(low ^ Integer.rotateRight(high, 0), 0);
		seedY = Integer.rotateRight(low ^ Integer.rotateRight(high, 8), 8);
		seedZ = Integer.rotateRight(low ^ Integer.rotateRight(high, 16), 16);
		seedW = Integer.rotateRight(low ^ Integer.rotateRight(high, 24), 24);

		randDataX = new int[0x10000];
		randDataY = new int[0x10000];

		Random rand = new Random(seed);
		for (int i = 0; i < randDataX.length; i++) {
			randDataX[i] = rand.nextInt();
			randDataY[i] = rand.nextInt();
		}
	}

	private float intToFloat(int i) {
		return (float) ((double) i / Integer.MAX_VALUE);
	}

	private int getRandomDataX(int i) {
		return randDataX[(i & 0xffff)] ^ randDataX[(i >> 16) & 0xffff];
	}

	private int getRandomDataY(int i) {
		return randDataY[(i & 0xffff)] ^ randDataY[(i >> 16) & 0xffff];
	}

	public float getValue(float x, float y) {
		int x0 = (int) Math.floor(x);
		int y0 = (int) Math.floor(y);

		x -= x0;
		y -= y0;

		float min = Float.MAX_VALUE;

		for (int dx = -1; dx < 2; dx++) {
			for (int dy = -1; dy < 2; dy++) {
				int rx = getRandomDataX(x0 + dx + seedX);
				rx = getRandomDataX(rx + y0 + dy + seedY);

				int ry = getRandomDataX(x0 + dx + seedX);
				ry = getRandomDataX(ry + y0 + dy + seedY);

				float cx = Math.abs(intToFloat(rx));
				float cy = Math.abs(intToFloat(ry));

				float dist = (float) Math.sqrt((cx - x + dx) * (cx - x + dx) + (cy - y + dy) * (cy - y + dy));

				min = Math.min(min, dist);
			}
		}

		return min;
	}

	@Override
	public float getValue(float x, float y, float z) {
		return 0;
	}

	@Override
	public float getValue(float x, float y, float z, float w) {
		return 0;
	}

	@Override
	public float getValueWithOctaves(float x, float y, int octaves) {
		return getValue(x, y);
	}

	@Override
	public float getValueWithOctaves(float x, float y, float z, int octaves) {
		return getValue(x, y, z);
	}

	@Override
	public float getValueWithOctaves(float x, float y, float z, float w, int octaves) {
		return getValue(x, y, z, w);
	}

}
