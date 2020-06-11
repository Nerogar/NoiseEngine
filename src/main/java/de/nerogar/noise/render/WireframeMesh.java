package de.nerogar.noise.render;

import de.nerogar.noise.render.Mesh;

public class WireframeMesh {

	private int indexCount;
	private int vertexCount;

	private int[]   indexArray;
	private float[] positionArray;

	public WireframeMesh(int indexCount, int vertexCount, int[] indexArray, float[] positionArray) {
		this.indexCount = indexCount;
		this.vertexCount = vertexCount;
		this.indexArray = indexArray;
		this.positionArray = positionArray;
	}

	public WireframeMesh(Mesh mesh) {
		createFromMesh(mesh);
	}

	private void createFromMesh(Mesh mesh) {
		float[] trianglePositionArray = mesh.getPositionArray();
		int[] triangleIndexArray = mesh.getIndexArray();
		int triangleCount = mesh.getTriangleCount();

		positionArray = new float[triangleCount * 9];
		indexArray = new int[triangleCount * 6];

		// create 3 lines for every triangle
		for (int i = 0; i < triangleCount; i++) {
			int triangleIndex0 = triangleIndexArray[i * 3];
			int triangleIndex1 = triangleIndexArray[i * 3 + 1];
			int triangleIndex2 = triangleIndexArray[i * 3 + 2];

			// create position vectors
			positionArray[i * 9] = trianglePositionArray[triangleIndex0 * 3];
			positionArray[i * 9 + 1] = trianglePositionArray[triangleIndex0 * 3 + 1];
			positionArray[i * 9 + 2] = trianglePositionArray[triangleIndex0 * 3 + 2];

			positionArray[i * 9 + 3] = trianglePositionArray[triangleIndex1 * 3];
			positionArray[i * 9 + 4] = trianglePositionArray[triangleIndex1 * 3 + 1];
			positionArray[i * 9 + 5] = trianglePositionArray[triangleIndex1 * 3 + 2];

			positionArray[i * 9 + 6] = trianglePositionArray[triangleIndex2 * 3];
			positionArray[i * 9 + 7] = trianglePositionArray[triangleIndex2 * 3 + 1];
			positionArray[i * 9 + 8] = trianglePositionArray[triangleIndex2 * 3 + 2];

			// create indices
			indexArray[i * 6] = i * 3;
			indexArray[i * 6 + 1] = i * 3 + 1;

			indexArray[i * 6 + 2] = i * 3 + 1;
			indexArray[i * 6 + 3] = i * 3 + 2;

			indexArray[i * 6 + 4] = i * 3 + 2;
			indexArray[i * 6 + 5] = i * 3;
		}

		indexCount = indexArray.length;
		vertexCount = triangleCount * 3;
	}

	public int getIndexCount()        { return indexCount; }

	public int getVertexCount()       { return vertexCount; }

	public int[] getIndexArray()      { return indexArray; }

	public float[] getPositionArray() { return positionArray; }

}
