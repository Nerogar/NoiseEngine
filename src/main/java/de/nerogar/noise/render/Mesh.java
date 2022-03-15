package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.math.MathHelper;
import de.nerogar.noise.math.Vector2f;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.util.Logger;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.IVector2f;
import de.nerogar.noiseInterface.math.IVector3f;

public class Mesh {

	private int indexCount;
	private int vertexCount;
	private int triangleCount;

	private int[]     indexArray;
	private float[]   positionArray;
	private float[]   uvArray;
	private float[]   normalArray;
	private float[]   tangentArray;
	private float[]   biTangentArray;
	private float[]   boneIndexArray;
	private float[]   boneWeightArray;
	private float[][] additionalAttributes;
	private int[]     additionalAttributeComponents;
	private float     boundingRadius;

	public Mesh(int indexCount, int vertexCount, int[] indexArray,
			float[] positionArray, float[] uvArray,
			float[] normalArray, float[] tangentArray, float[] biTangentArray,
			float[] boneIndexArray, float[] boneWeightArray
	           ) {

		this.indexCount = indexCount;
		this.vertexCount = vertexCount;
		this.indexArray = indexArray;

		this.positionArray = positionArray;
		this.uvArray = uvArray;

		this.normalArray = normalArray;
		this.tangentArray = tangentArray;
		this.biTangentArray = biTangentArray;

		this.boneIndexArray = boneIndexArray;
		this.boneWeightArray = boneWeightArray;

		calcBoundingRadius();
		calcTriangleCount();
	}

	public Mesh(int indexCount, int vertexCount, int[] indexArray, float[] positionArray, float[] uvArray, float[] normalArray) {
		this.indexCount = indexCount;
		this.vertexCount = vertexCount;

		this.indexArray = indexArray;
		this.positionArray = positionArray;
		this.uvArray = uvArray;
		this.normalArray = normalArray;

		calcBoundingRadius();
		calcTriangleCount();
	}

	public Mesh(int indexCount, int vertexCount, int[] indexArray, float[] positionArray, float[] uvArray) {
		this.indexCount = indexCount;
		this.vertexCount = vertexCount;

		this.indexArray = indexArray;
		this.positionArray = positionArray;
		this.uvArray = uvArray;

		calcBoundingRadius();
		calcTriangleCount();
	}

	private void calcNormals() {
		normalArray = new float[vertexCount * 3];

		IVector3f direction1 = new Vector3f();
		IVector3f direction2 = new Vector3f();

		// calculate normals
		for (int i = 0; i < indexCount; i += 3) {
			direction1.set(
					positionArray[indexArray[i + 1] * 3 + 0] - positionArray[indexArray[i + 0] * 3 + 0],
					positionArray[indexArray[i + 1] * 3 + 1] - positionArray[indexArray[i + 0] * 3 + 1],
					positionArray[indexArray[i + 1] * 3 + 2] - positionArray[indexArray[i + 0] * 3 + 2]
			              );

			direction2.set(
					positionArray[indexArray[i + 2] * 3 + 0] - positionArray[indexArray[i + 0] * 3 + 0],
					positionArray[indexArray[i + 2] * 3 + 1] - positionArray[indexArray[i + 0] * 3 + 1],
					positionArray[indexArray[i + 2] * 3 + 2] - positionArray[indexArray[i + 0] * 3 + 2]
			              );

			direction1.cross(direction2);
			direction1.normalize();

			for (int j = 0; j < 3; j++) {
				normalArray[indexArray[i + j] * 3 + 0] += direction1.getX();
				normalArray[indexArray[i + j] * 3 + 1] += direction1.getY();
				normalArray[indexArray[i + j] * 3 + 2] += direction1.getZ();
			}
		}

		// normalize normals
		for (int i = 0; i < vertexCount; i++) {
			direction1.set(normalArray[i * 3 + 0], normalArray[i * 3 + 1], normalArray[i * 3 + 2]);
			direction1.normalize();
			normalArray[i * 3 + 0] = direction1.getX();
			normalArray[i * 3 + 1] = direction1.getY();
			normalArray[i * 3 + 2] = direction1.getZ();
		}
	}

	private void calcTangents() {
		tangentArray = new float[vertexCount * 3];
		biTangentArray = new float[vertexCount * 3];

		// edge vectors on the texture
		IVector2f sRelative = new Vector2f();
		IVector2f tRelative = new Vector2f();

		// edge vectors on the mesh
		IVector3f q1Relative = new Vector3f();
		IVector3f q2Relative = new Vector3f();

		IVector3f tangent = new Vector3f();
		IVector3f bitangent = new Vector3f();
		IVector3f normal = new Vector3f();

		for (int i = 0; i < indexCount; i += 3) {
			// calculate tangents and bitangents

			sRelative.set(
					uvArray[indexArray[i + 1] * 2 + 0] - uvArray[indexArray[i + 0] * 2 + 0],
					uvArray[indexArray[i + 2] * 2 + 0] - uvArray[indexArray[i + 0] * 2 + 0]
			             );

			tRelative.set(
					uvArray[indexArray[i + 1] * 2 + 1] - uvArray[indexArray[i + 0] * 2 + 1],
					uvArray[indexArray[i + 2] * 2 + 1] - uvArray[indexArray[i + 0] * 2 + 1]
			             );

			q1Relative.set(
					positionArray[indexArray[i + 1] * 3 + 0] - positionArray[indexArray[i + 0] * 3 + 0],
					positionArray[indexArray[i + 1] * 3 + 1] - positionArray[indexArray[i + 0] * 3 + 1],
					positionArray[indexArray[i + 1] * 3 + 2] - positionArray[indexArray[i + 0] * 3 + 2]
			              );

			q2Relative.set(
					positionArray[indexArray[i + 2] * 3 + 0] - positionArray[indexArray[i + 0] * 3 + 0],
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

				biTangentArray[indexArray[i + j] * 3 + 0] += bitangent.getX();
				biTangentArray[indexArray[i + j] * 3 + 1] += bitangent.getY();
				biTangentArray[indexArray[i + j] * 3 + 2] += bitangent.getZ();
			}

		}

		float maxError = 0;
		float avgError = 0;
		float minError = 0;

		// Use the Gram–Schmidt process to make normals, tangents and bitangents orthogonal.
		// Normals are expected to be normalized, tangents and bitangents are normalized in the process
		for (int i = 0; i < vertexCount; i++) {
			tangent.set(tangentArray[i * 3 + 0], tangentArray[i * 3 + 1], tangentArray[i * 3 + 2]);
			bitangent.set(biTangentArray[i * 3 + 0], biTangentArray[i * 3 + 1], biTangentArray[i * 3 + 2]);

			// make tangent orthogonal and store
			normal.set(normalArray[i * 3 + 0], normalArray[i * 3 + 1], normalArray[i * 3 + 2]);

			tangent.subtract(normal.multiply(normal.dot(tangent)));
			tangent.normalize();
			tangentArray[i * 3 + 0] = tangent.getX();
			tangentArray[i * 3 + 1] = tangent.getY();
			tangentArray[i * 3 + 2] = tangent.getZ();

			// make bitangent orthogonal and store
			normal.set(normalArray[i * 3], normalArray[i * 3 + 1], normalArray[i * 3 + 2]);
			bitangent.subtract(normal.multiply(normal.dot(bitangent)).add(tangent.multiply(tangent.dot(bitangent))));
			bitangent.normalize();
			biTangentArray[i * 3 + 0] = bitangent.getX();
			biTangentArray[i * 3 + 1] = bitangent.getY();
			biTangentArray[i * 3 + 2] = bitangent.getZ();

			// load normal and tangent again to calculate errors
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

		avgError /= (float) vertexCount * 3f;

		if (maxError > 1.0e-3f || Float.isNaN(maxError)) {
			Noise.getLogger().log(Logger.WARNING, "Problem calculating mesh tangent space: (minError:" + minError + ", avgError:" + avgError + ", maxError:" + maxError + ")");
		}

	}

	private void calcBoundingRadius() {

		for (int i = 0; i < vertexCount; i += 3) {
			float tempBoundingSize = positionArray[i + 0] * positionArray[i + 0] + positionArray[i + 1] * positionArray[i + 1] + positionArray[i + 2] * positionArray[i + 2];

			boundingRadius = Math.max(boundingRadius, tempBoundingSize);
		}

		boundingRadius = (float) Math.sqrt(boundingRadius);
	}

	private void calcTriangleCount() {
		triangleCount = indexCount / 3;
	}

	public Float calculateIntersection(Ray ray) {
		Float intersectionDistance = null;

		float rayOriginX = ray.getStart().getX();
		float rayOriginY = ray.getStart().getY();
		float rayOriginZ = ray.getStart().getZ();

		float rayDirX = ray.getDir().getX();
		float rayDirY = ray.getDir().getY();
		float rayDirZ = ray.getDir().getZ();

		for (int triangle = 0; triangle < triangleCount; triangle++) {
			int index0 = indexArray[triangle * 3 + 0];
			int index1 = indexArray[triangle * 3 + 1];
			int index2 = indexArray[triangle * 3 + 2];

			Float currentIntersectionDistance = MathHelper.rayTriangleIntersectionCulling(
					rayOriginX, rayOriginY, rayOriginZ,
					rayDirX, rayDirY, rayDirZ,
					positionArray[index0 * 3 + 0], positionArray[index0 * 3 + 1], positionArray[index0 * 3 + 2],
					positionArray[index1 * 3 + 0], positionArray[index1 * 3 + 1], positionArray[index1 * 3 + 2],
					positionArray[index2 * 3 + 0], positionArray[index2 * 3 + 1], positionArray[index2 * 3 + 2]
			                                                                             );

			if (currentIntersectionDistance == null) continue;
			if (currentIntersectionDistance < 0) continue;
			if (intersectionDistance == null || currentIntersectionDistance < intersectionDistance) intersectionDistance = currentIntersectionDistance;
		}

		return intersectionDistance;
	}

	public void setIndexCount(int indexCount)           { this.indexCount = indexCount;}

	public int getIndexCount()                          { return indexCount; }

	public void setVertexCount(int vertexCount)         { this.vertexCount = vertexCount; calcTriangleCount(); calcBoundingRadius();}

	public int getVertexCount()                         { return vertexCount; }

	public void setIndexArray(int[] indexArray)         { this.indexArray = indexArray; }

	public int[] getIndexArray()                        { return indexArray; }

	public void setPositionArray(float[] positionArray) { this.positionArray = positionArray; calcTriangleCount(); calcBoundingRadius();}

	public float[] getPositionArray()                   { return positionArray; }

	public void setUvArray(float[] uvArray)             { this.uvArray = uvArray; }

	public float[] getUVArray()                         { return uvArray; }

	public void setNormalArray(float[] normalArray) {
		this.normalArray = normalArray;
		this.tangentArray = null;
		this.biTangentArray = null;
	}

	public float[] getNormalArray() {
		if (normalArray == null) {
			calcNormals();
		}

		return normalArray;
	}

	public void setTangentArray(float[] tangentArray) {
		this.tangentArray = tangentArray;
	}

	public float[] getTangentArray() {
		if (tangentArray == null) {
			if (normalArray == null) {
				calcNormals();
			}
			calcTangents();
		}

		return tangentArray;
	}

	public void setBiTangentArray(float[] biTangentArray) {
		this.biTangentArray = biTangentArray;
	}

	public float[] getBiTangentArray() {
		if (biTangentArray == null) {
			if (normalArray == null) {
				calcNormals();
			}
			calcTangents();
		}

		return biTangentArray;
	}

	public void setNormalAndTangentArrays(float[] normalArray, float[] tangentArray, float[] biTangentArray) {
		this.normalArray = normalArray;
		this.tangentArray = tangentArray;
		this.biTangentArray = biTangentArray;
	}

	public void setBoneWeightArray(float[] boneWeightArray)                           { this.boneWeightArray = boneWeightArray; }

	public float[] getBoneWeightArray()                                               { return boneWeightArray; }

	public void setBoneIndexArray(float[] boneIndexArray)                             { this.boneIndexArray = boneIndexArray; }

	public float[] getBoneIndexArray()                                                { return boneIndexArray; }

	public void setAdditionalAttributes(float[]... additionalAttributes)              { this.additionalAttributes = additionalAttributes; }

	public float[][] getAdditionalAttributes()                                        { return additionalAttributes; }

	public void setAdditionalAttributeComponents(int[] additionalAttributeComponents) { this.additionalAttributeComponents = additionalAttributeComponents; }

	public int[] getAdditionalAttributeComponents()                                   { return additionalAttributeComponents; }

	public float getBoundingRadius()                                                  { return boundingRadius; }

	public int getTriangleCount()                                                     { return triangleCount; }

	public void clearArrays() {
		indexArray = null;
		positionArray = null;
		uvArray = null;
		normalArray = null;
		tangentArray = null;
		biTangentArray = null;
		boneIndexArray = null;
		boneWeightArray = null;
		additionalAttributes = null;
	}

}
