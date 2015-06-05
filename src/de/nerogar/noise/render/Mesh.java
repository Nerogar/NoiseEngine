package de.nerogar.noise.render;

public class Mesh {

	private int[] indexArray;
	private float[] positionArray;
	private float[] uvArray;
	private float[] normalArray;
	private float minBoundingSize;

	public Mesh(int[] indexArray, float[] positionArray, float[] uvArray, float[] normalArray, float minBoundingSize) {
		this.indexArray = indexArray;
		this.positionArray = positionArray;
		this.uvArray = uvArray;
		this.normalArray = normalArray;
		this.minBoundingSize = minBoundingSize;
	}

	public int[] getIndexArray() {
		return indexArray;
	}

	public float[] getPositionArray() {
		return positionArray;
	}

	public float[] getUVArray() {
		return uvArray;
	}

	public float[] getNormalArray() {
		return normalArray;
	}

	public float getMinBoundingSize() {
		return minBoundingSize;
	}

}
