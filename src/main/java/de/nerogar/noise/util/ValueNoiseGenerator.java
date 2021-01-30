package de.nerogar.noise.util;

import de.nerogar.noiseInterface.util.INoiseGenerator;

import java.util.Random;

import static de.nerogar.noise.math.MathHelper.mix;

public class ValueNoiseGenerator implements INoiseGenerator {

	private long seed;

	private int seedX;
	private int seedY;
	private int seedZ;
	private int seedW;

	private int[] randData;

	public ValueNoiseGenerator(long seed) {
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

		randData = new int[0x10000];

		Random rand = new Random(seed);
		for (int i = 0; i < randData.length; i++) {
			randData[i] = rand.nextInt();
		}
	}

	private float fade(float t) {
		return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
	}

	private float intToFloat(int i) {
		return (float) ((double) (Math.abs(i) & 0x7fffffff) / 0x7fffffff);
	}

	private int getRandomData(int i) {
		return randData[(i & 0xffff)] ^ randData[(i >> 16) & 0xffff];
	}

	@Override
	public float getValue(float x, float y) {
		int x0 = (int) Math.floor(x);
		int y0 = (int) Math.floor(y);

		x -= x0;
		y -= y0;

		x = fade(x);
		y = fade(y);

		int r0 = getRandomData(x0 + seedX);
		int r1 = getRandomData(x0 + 1 + seedX);

		float r00 = intToFloat(getRandomData(r0 + y0 + seedY));
		float r01 = intToFloat(getRandomData(r0 + y0 + 1 + seedY));
		float r10 = intToFloat(getRandomData(r1 + y0 + seedY));
		float r11 = intToFloat(getRandomData(r1 + y0 + 1 + seedY));

		float m0 = mix(r00, r01, y);
		float m1 = mix(r10, r11, y);

		return mix(m0, m1, x);
	}

	@Override
	public float getValue(float x, float y, float z) {
		int x0 = (int) Math.floor(x);
		int y0 = (int) Math.floor(y);
		int z0 = (int) Math.floor(z);

		x -= x0;
		y -= y0;
		z -= z0;

		int r0 = getRandomData(x0 + seedX);
		int r1 = getRandomData(x0 + 1 + seedX);

		int r00 = getRandomData(r0 + y0 + seedY);
		int r01 = getRandomData(r0 + y0 + 1 + seedY);
		int r10 = getRandomData(r1 + y0 + seedY);
		int r11 = getRandomData(r1 + y0 + 1 + seedY);

		float r000 = intToFloat(getRandomData(r00 + z0 + seedZ));
		float r001 = intToFloat(getRandomData(r00 + z0 + 1 + seedZ));
		float r010 = intToFloat(getRandomData(r01 + z0 + seedZ));
		float r011 = intToFloat(getRandomData(r01 + z0 + 1 + seedZ));
		float r100 = intToFloat(getRandomData(r10 + z0 + seedZ));
		float r101 = intToFloat(getRandomData(r10 + z0 + 1 + seedZ));
		float r110 = intToFloat(getRandomData(r11 + z0 + seedZ));
		float r111 = intToFloat(getRandomData(r11 + z0 + 1 + seedZ));

		float m00 = mix(r000, r001, z);
		float m01 = mix(r010, r011, z);
		float m10 = mix(r100, r101, z);
		float m11 = mix(r110, r111, z);

		float m0 = mix(m00, m01, y);
		float m1 = mix(m10, m11, y);

		return mix(m0, m1, x);
	}

	@Override
	public float getValue(float x, float y, float z, float w) {

		int x0 = (int) Math.floor(x);
		int y0 = (int) Math.floor(y);
		int z0 = (int) Math.floor(z);
		int w0 = (int) Math.floor(w);

		x -= x0;
		y -= y0;
		z -= z0;
		w -= w0;

		int r0 = getRandomData(x0 + seedX);
		int r1 = getRandomData(x0 + 1 + seedX);

		int r00 = getRandomData(r0 + y0 + seedY);
		int r01 = getRandomData(r0 + y0 + 1 + seedY);
		int r10 = getRandomData(r1 + y0 + seedY);
		int r11 = getRandomData(r1 + y0 + 1 + seedY);

		int r000 = getRandomData(r00 + z0 + seedZ);
		int r001 = getRandomData(r00 + z0 + 1 + seedZ);
		int r010 = getRandomData(r01 + z0 + seedZ);
		int r011 = getRandomData(r01 + z0 + 1 + seedZ);
		int r100 = getRandomData(r10 + z0 + seedZ);
		int r101 = getRandomData(r10 + z0 + 1 + seedZ);
		int r110 = getRandomData(r11 + z0 + seedZ);
		int r111 = getRandomData(r11 + z0 + 1 + seedZ);

		float r0000 = intToFloat(getRandomData(r000 + w0 + seedW));
		float r0001 = intToFloat(getRandomData(r000 + w0 + 1 + seedW));
		float r0010 = intToFloat(getRandomData(r001 + w0 + seedW));
		float r0011 = intToFloat(getRandomData(r001 + w0 + 1 + seedW));
		float r0100 = intToFloat(getRandomData(r010 + w0 + seedW));
		float r0101 = intToFloat(getRandomData(r010 + w0 + 1 + seedW));
		float r0110 = intToFloat(getRandomData(r011 + w0 + seedW));
		float r0111 = intToFloat(getRandomData(r011 + w0 + 1 + seedW));
		float r1000 = intToFloat(getRandomData(r100 + w0 + seedW));
		float r1001 = intToFloat(getRandomData(r100 + w0 + 1 + seedW));
		float r1010 = intToFloat(getRandomData(r101 + w0 + seedW));
		float r1011 = intToFloat(getRandomData(r101 + w0 + 1 + seedW));
		float r1100 = intToFloat(getRandomData(r110 + w0 + seedW));
		float r1101 = intToFloat(getRandomData(r110 + w0 + 1 + seedW));
		float r1110 = intToFloat(getRandomData(r111 + w0 + seedW));
		float r1111 = intToFloat(getRandomData(r111 + w0 + 1 + seedW));

		float m000 = mix(r0000, r0001, w);
		float m001 = mix(r0010, r0011, w);
		float m010 = mix(r0100, r0101, w);
		float m011 = mix(r0110, r0111, w);
		float m100 = mix(r1000, r1001, w);
		float m101 = mix(r1010, r1011, w);
		float m110 = mix(r1100, r1101, w);
		float m111 = mix(r1110, r1111, w);

		float m00 = mix(m000, m001, z);
		float m01 = mix(m010, m011, z);
		float m10 = mix(m100, m101, z);
		float m11 = mix(m110, m111, z);

		float m0 = mix(m00, m01, y);
		float m1 = mix(m10, m11, y);

		return mix(m0, m1, x);
	}

	@Override
	public float getValueWithOctaves(float x, float y, int octaves) {
		int octaves2 = (1 << (octaves - 1));
		float amplitude = (float) octaves2 / (octaves2 + octaves2 - 1);
		float result = 0;
		for (int i = 0; i < octaves; i++) {
			float size = 1 << i;
			result += getValue(x * size, y * size) * amplitude;
			amplitude /= 2;
		}
		return result;
	}

	@Override
	public float getValueWithOctaves(float x, float y, float z, int octaves) {
		int octaves2 = (1 << (octaves - 1));
		float amplitude = (float) octaves2 / (octaves2 + octaves2 - 1);
		float result = 0;
		for (int i = 0; i < octaves; i++) {
			float size = 1 << i;
			result += getValue(x * size, y * size, z * size) * amplitude;
			amplitude /= 2;
		}
		return result;
	}

	@Override
	public float getValueWithOctaves(float x, float y, float z, float w, int octaves) {
		int octaves2 = (1 << (octaves - 1));
		float amplitude = (float) octaves2 / (octaves2 + octaves2 - 1);
		float result = 0;
		for (int i = 0; i < octaves; i++) {
			float size = 1 << i;
			result += getValue(x * size, y * size, z * size, w * size) * amplitude;
			amplitude /= 2;
		}
		return result;
	}

}
