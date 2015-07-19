package de.nerogar.noise.render;

import de.nerogar.noise.util.*;

public class Mesh {

	private int indexCount;
	private int vertexCount;

	private int[] indexArray;
	private float[] positionArray;
	private float[] uvArray;
	private float[] normalArray;
	private float[] tangentArray;
	private float[] bitangentArray;
	private float boundingRadius;

	public Mesh(int indexCount, int vertexCount, int[] indexArray, float[] positionArray, float[] uvArray, float[] normalArray, float[] tangentArray, float[] bitangentArray) {
		this.indexCount = indexCount;
		this.vertexCount = vertexCount;

		this.indexArray = indexArray;
		this.positionArray = positionArray;
		this.uvArray = uvArray;
		this.normalArray = normalArray;
		this.tangentArray = tangentArray;
		this.bitangentArray = bitangentArray;

		calcBoundingRadius();
	}

	public Mesh(int indexCount, int vertexCount, int[] indexArray, float[] positionArray, float[] uvArray, float[] normalArray) {
		this.indexCount = indexCount;
		this.vertexCount = vertexCount;

		this.indexArray = indexArray;
		this.positionArray = positionArray;
		this.uvArray = uvArray;
		this.normalArray = normalArray;

		calcTangents();
		calcBoundingRadius();
	}

	public int getIndexCount() {
		return indexCount;
	}

	public int getVertexCount() {
		return vertexCount;
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

	public float[] getTangentArray() {
		return tangentArray;
	}

	public float[] getBitangentArray() {
		return bitangentArray;
	}

	public float getboundingRadius() {
		return boundingRadius;
	}

	private void calcTangents() {
		tangentArray = new float[normalArray.length];
		bitangentArray = new float[normalArray.length];

		Vector2f sRelative = new Vector2f();
		Vector2f tRelative = new Vector2f();

		Vector3f q1Relative = new Vector3f();
		Vector3f q2Relative = new Vector3f();

		Vector3f tangent = new Vector3f();
		Vector3f bitangent = new Vector3f();
		Vector3f normal = new Vector3f();

		for (int i = 0; i < indexCount; i += 3) {
			//caclulate tangents and bitangents

			sRelative.set(
					uvArray[indexArray[i + 1] * 2] - uvArray[indexArray[i + 0] * 2],
					uvArray[indexArray[i + 2] * 2] - uvArray[indexArray[i + 0] * 2]
					);

			tRelative.set(
					uvArray[indexArray[i + 1] * 2 + 1] - uvArray[indexArray[i + 0] * 2 + 1],
					uvArray[indexArray[i + 2] * 2 + 1] - uvArray[indexArray[i + 0] * 2 + 1]
					);

			q1Relative.set(
					positionArray[indexArray[i + 1] * 3] - positionArray[indexArray[i + 0] * 3],
					positionArray[indexArray[i + 1] * 3 + 1] - positionArray[indexArray[i + 0] * 3 + 1],
					positionArray[indexArray[i + 1] * 3 + 2] - positionArray[indexArray[i + 0] * 3 + 2]
					);

			q2Relative.set(
					positionArray[indexArray[i + 2] * 3] - positionArray[indexArray[i + 0] * 3],
					positionArray[indexArray[i + 2] * 3 + 1] - positionArray[indexArray[i + 0] * 3 + 1],
					positionArray[indexArray[i + 2] * 3 + 2] - positionArray[indexArray[i + 0] * 3 + 2]
					);

			tangent.set(
					(tRelative.getY() * q1Relative.getX() - tRelative.getX() * q2Relative.getX()),
					(tRelative.getY() * q1Relative.getY() - tRelative.getX() * q2Relative.getY()),
					(tRelative.getY() * q1Relative.getZ() - tRelative.getX() * q2Relative.getZ())
					);

			bitangent.set(
					(-sRelative.getY() * q1Relative.getX() + sRelative.getX() * q2Relative.getX()),
					(-sRelative.getY() * q1Relative.getY() + sRelative.getX() * q2Relative.getY()),
					(-sRelative.getY() * q1Relative.getZ() + sRelative.getX() * q2Relative.getZ())
					);

			tangent.normalize();
			bitangent.normalize();

			for (int j = 0; j < 3; j++) {
				tangentArray[indexArray[i + j] * 3 + 0] += tangent.getX();
				tangentArray[indexArray[i + j] * 3 + 1] += tangent.getY();
				tangentArray[indexArray[i + j] * 3 + 2] += tangent.getZ();

				bitangentArray[indexArray[i + j] * 3 + 0] += bitangent.getX();
				bitangentArray[indexArray[i + j] * 3 + 1] += bitangent.getY();
				bitangentArray[indexArray[i + j] * 3 + 2] += bitangent.getZ();
			}

		}

		float maxError = 0;
		float avgError = 0;
		float minError = 0;

		//normalize tangent and bitangent
		for (int i = 0; i < vertexCount; i++) {
			tangent.set(tangentArray[i * 3], tangentArray[i * 3 + 1], tangentArray[i * 3 + 2]);
			bitangent.set(bitangentArray[i * 3], bitangentArray[i * 3 + 1], bitangentArray[i * 3 + 2]);

			//make tangent orthogonal and store
			normal.set(normalArray[i * 3], normalArray[i * 3 + 1], normalArray[i * 3 + 2]);

			tangent.subtract(normal.multiply(normal.dot(tangent)));
			tangent.normalize();
			tangentArray[i * 3 + 0] = tangent.getX();
			tangentArray[i * 3 + 1] = tangent.getY();
			tangentArray[i * 3 + 2] = tangent.getZ();

			//make bitangent orthogonal and store
			normal.set(normalArray[i * 3], normalArray[i * 3 + 1], normalArray[i * 3 + 2]);
			bitangent.subtract(normal.multiply(normal.dot(bitangent)).add(tangent.multiply(tangent.dot(bitangent))));
			bitangent.normalize();
			bitangentArray[i * 3 + 0] = bitangent.getX();
			bitangentArray[i * 3 + 1] = bitangent.getY();
			bitangentArray[i * 3 + 2] = bitangent.getZ();

			normal.set(normalArray[i * 3], normalArray[i * 3 + 1], normalArray[i * 3 + 2]);
			tangent.set(tangentArray[i * 3], tangentArray[i * 3 + 1], tangentArray[i * 3 + 2]);

			float error1 = Math.abs(normal.dot(tangent));
			float error2 = Math.abs(tangent.dot(bitangent));
			float error3 = Math.abs(bitangent.dot(normal));

			maxError = Math.max(maxError, error1);
			maxError = Math.max(maxError, error2);
			maxError = Math.max(maxError, error3);

			minError = Math.min(minError, error1);
			minError = Math.min(minError, error2);
			minError = Math.min(minError, error3);

			avgError += error1 + error2 + error3;
		}

		avgError /= (float) vertexCount;

		if (maxError > 1.0e-3f) {
			Logger.log(Logger.WARNING, "Problem calculating mesh tangent space: (minError:" + minError + ", avgError:" + avgError + ", maxError:" + maxError + ")");
		}

	}

	private void calcBoundingRadius() {

		for (int i = 0; i < positionArray.length; i += 3) {
			float tempBoundingSize = positionArray[i + 0] * positionArray[i + 0] + positionArray[i + 1] * positionArray[i + 1] + positionArray[i + 2] * positionArray[i + 2];

			if (tempBoundingSize > boundingRadius) boundingRadius = tempBoundingSize;
		}

		boundingRadius = (float) Math.sqrt(boundingRadius);
	}
}
