package de.nerogar.noise.render;

public class VertexList {

	float[] positionList;
	float[] uvList;
	float[] normalList;

	int[] indexList;

	private int vertexCount, maxVertices;
	private int indexCount;

	public VertexList() {
		maxVertices = 16;

		positionList = new float[maxVertices * 3];
		uvList = new float[maxVertices * 2];
		normalList = new float[maxVertices * 3];

		indexList = new int[16];
	}

	public void clear() {
		vertexCount = 0;
		indexCount = 0;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int[] getIndexArray() {
		return indexList;
	}

	public float[] getPositionArray() {
		return positionList;
	}

	public float[] getUVArray() {
		return uvList;
	}

	public float[] getNormalArray() {
		return normalList;
	}

	public void addIndex(int index) {
		ensureIndexCapacity(indexCount + 1);
		indexList[indexCount++] = index;
	}

	public void addIndex(int index1, int index2) {
		ensureIndexCapacity(indexCount + 2);
		indexList[indexCount++] = index1;
		indexList[indexCount++] = index2;
	}

	public void addIndex(int index1, int index2, int index3) {
		ensureIndexCapacity(indexCount + 3);
		indexList[indexCount++] = index1;
		indexList[indexCount++] = index2;
		indexList[indexCount++] = index3;
	}

	public int addVertex(float p1, float p2, float p3, float uv1, float uv2, float n1, float n2, float n3) {
		ensureVertexCapacity(vertexCount + 1);

		positionList[vertexCount * 3 + 0] = p1;
		positionList[vertexCount * 3 + 1] = p2;
		positionList[vertexCount * 3 + 2] = p3;

		uvList[vertexCount * 2 + 0] = uv1;
		uvList[vertexCount * 2 + 1] = uv2;

		normalList[vertexCount * 3 + 0] = n1;
		normalList[vertexCount * 3 + 1] = n2;
		normalList[vertexCount * 3 + 2] = n3;

		return vertexCount++;
	}

	private void ensureVertexCapacity(int newSize) {
		if (maxVertices < newSize) {
			newSize = (int) (newSize * 1.5f);

			maxVertices = newSize;

			float[] newPositionList = new float[newSize * 3];
			float[] newUVList = new float[newSize * 2];
			float[] newNormalList = new float[newSize * 3];

			System.arraycopy(positionList, 0, newPositionList, 0, positionList.length);
			System.arraycopy(uvList, 0, newUVList, 0, uvList.length);
			System.arraycopy(normalList, 0, newNormalList, 0, normalList.length);

			positionList = newPositionList;
			uvList = newUVList;
			normalList = newNormalList;
		}
	}

	private void ensureIndexCapacity(int newSize) {
		if (indexList.length < newSize) {
			newSize = (int) (newSize * 1.5f);

			int[] newIndexList = new int[newSize];

			System.arraycopy(indexList, 0, newIndexList, 0, indexList.length);

			indexList = newIndexList;
		}
	}

}
