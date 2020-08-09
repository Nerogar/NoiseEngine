package de.nerogar.noiseInterface.util;

public interface INoiseGenerator {

	float getValue(float x, float y);

	float getValue(float x, float y, float z);

	float getValue(float x, float y, float z, float w);

	float getValueWithOctaves(float x, float y, int octaves);

	float getValueWithOctaves(float x, float y, float z, int octaves);

	float getValueWithOctaves(float x, float y, float z, float w, int octaves);
}
