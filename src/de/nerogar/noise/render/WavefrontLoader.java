package de.nerogar.noise.render;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

public class WavefrontLoader {

	private static class VertexTuple {
		public int vertex, normal, texCoord;
		public boolean uvDirection;

		public VertexTuple(int vertex, int texCoord, int normal, boolean uvDirection) {
			this.vertex = vertex;
			this.texCoord = texCoord;
			this.normal = normal;
			this.uvDirection = uvDirection;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + vertex;
			hash = hash * 19 + texCoord;
			hash = hash * 23 + normal;

			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof VertexTuple) {
				VertexTuple otherTuple = (VertexTuple) obj;

				return this.vertex == otherTuple.vertex
						&& this.texCoord == otherTuple.texCoord
						&& this.normal == otherTuple.normal
						&& this.uvDirection == otherTuple.uvDirection;
			}

			return false;
		}

	}

	private static HashMap<String, Mesh> meshMap = new HashMap<String, Mesh>();

	public static Mesh loadObject(String filename) {

		Mesh object = meshMap.get(filename);
		if (object != null) return object;

		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> normals = new ArrayList<float[]>();
		ArrayList<float[]> texCoords = new ArrayList<float[]>();

		ArrayList<VertexTuple> vertexTuples = new ArrayList<VertexTuple>();
		HashMap<VertexTuple, Integer> vertexTupleMap = new HashMap<WavefrontLoader.VertexTuple, Integer>();

		ArrayList<int[]> faces = new ArrayList<int[]>();

		try {

			BufferedReader reader = new BufferedReader(new FileReader(filename));

			String line;
			while ((line = reader.readLine()) != null) {

				String[] lineSplit = line.split(" ");

				switch (lineSplit[0]) {
				case "v":
					if (lineSplit.length == 4) {
						float f1 = Float.parseFloat(lineSplit[1]);
						float f2 = Float.parseFloat(lineSplit[2]);
						float f3 = Float.parseFloat(lineSplit[3]);

						vertices.add(new float[] { f1, f2, f3 });
					}
					break;
				case "vt":
					if (lineSplit.length == 3) {
						float f1 = Float.parseFloat(lineSplit[1]);
						float f2 = Float.parseFloat(lineSplit[2]);

						texCoords.add(new float[] { f1, f2 });
					}

					break;
				case "vn":
					if (lineSplit.length == 4) {
						float f1 = Float.parseFloat(lineSplit[1]);
						float f2 = Float.parseFloat(lineSplit[2]);
						float f3 = Float.parseFloat(lineSplit[3]);

						normals.add(new float[] { f1, f2, f3 });
					}
					break;
				case "f":
					String lineData = line.substring(2);
					lineSplit = lineData.split(" ");

					if (lineSplit.length == 3) {
						String[][] lineSubSplit = new String[3][];

						lineSubSplit[0] = lineSplit[0].split("/");
						lineSubSplit[1] = lineSplit[1].split("/");
						lineSubSplit[2] = lineSplit[2].split("/");

						int[] tupleIndices = new int[3];

						boolean uvDirection = calcUVDirection(texCoords, Integer.parseInt(lineSubSplit[0][1]) - 1, Integer.parseInt(lineSubSplit[1][1]) - 1, Integer.parseInt(lineSubSplit[2][1]) - 1);

						vertexLoop: for (int i = 0; i < 3; i++) {
							if (lineSubSplit[i].length != 3) throw new RuntimeException("Unreadable wavefront file.");

							int f1 = Integer.parseInt(lineSubSplit[i][0]) - 1;
							int f2 = Integer.parseInt(lineSubSplit[i][1]) - 1;
							int f3 = Integer.parseInt(lineSubSplit[i][2]) - 1;

							VertexTuple newTuple = new VertexTuple(f1, f2, f3, uvDirection);

							//find existing tuple
							Integer tupleIndex = vertexTupleMap.get(newTuple);
							if (tupleIndex != null) {
								tupleIndices[i] = tupleIndex;
								continue vertexLoop;
							}

							//if no tuple was found
							vertexTuples.add(newTuple);
							tupleIndices[i] = vertexTuples.size() - 1;
							vertexTupleMap.put(newTuple, vertexTuples.size() - 1);
						}

						faces.add(tupleIndices);

					}

					break;
				}

			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//init object

		float[] verticesFinal = new float[vertexTuples.size() * 3];
		float[] normalsFinal = new float[vertexTuples.size() * 3];
		float[] texCoordsFinal = new float[vertexTuples.size() * 2];
		int[] indices = new int[faces.size() * 3];

		for (int i = 0; i < vertexTuples.size(); i++) {
			VertexTuple tuple = vertexTuples.get(i);

			verticesFinal[i * 3 + 0] = vertices.get(tuple.vertex)[0];
			verticesFinal[i * 3 + 1] = vertices.get(tuple.vertex)[1];
			verticesFinal[i * 3 + 2] = vertices.get(tuple.vertex)[2];

			normalsFinal[i * 3 + 0] = normals.get(tuple.normal)[0];
			normalsFinal[i * 3 + 1] = normals.get(tuple.normal)[1];
			normalsFinal[i * 3 + 2] = normals.get(tuple.normal)[2];

			texCoordsFinal[i * 2 + 0] = texCoords.get(tuple.texCoord)[0];
			texCoordsFinal[i * 2 + 1] = texCoords.get(tuple.texCoord)[1];
		}

		for (int i = 0; i < faces.size(); i++) {
			int[] face = faces.get(i);

			indices[i * 3 + 0] = face[0];
			indices[i * 3 + 1] = face[1];
			indices[i * 3 + 2] = face[2];
		}

		object = new Mesh(indices.length, vertexTuples.size(), indices, verticesFinal, texCoordsFinal, normalsFinal);

		meshMap.put(filename, object);

		Noise.getLogger().log(Logger.INFO, "loaded .obj file: " + filename);

		return object;
	}

	private static boolean calcUVDirection(ArrayList<float[]> texCoords, int uIndex, int vIndex, int wIndex) {
		float ux = texCoords.get(vIndex)[0] - texCoords.get(uIndex)[0];
		float uy = texCoords.get(vIndex)[1] - texCoords.get(uIndex)[1];
		float vx = texCoords.get(wIndex)[0] - texCoords.get(vIndex)[0];
		float vy = texCoords.get(wIndex)[1] - texCoords.get(vIndex)[1];

		float orthx = -uy;
		float orthy = ux;

		float dot = orthx * vx + orthy * vy;

		return dot < 0;
	}
}
