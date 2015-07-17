package de.nerogar.noise.render;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class WavefrontLoader {

	private static class VertexTuple {
		public int vertex, normal, texCoord;

		public VertexTuple(int vertex, int texCoord, int normal) {
			this.vertex = vertex;
			this.texCoord = texCoord;
			this.normal = normal;
		}

		public boolean equals(int vertex, int texCoord, int normal) {
			//return this.vertex == vertex && this.texCoord == texCoord && this.normal == normal;
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

						vertexLoop: for (int i = 0; i < lineSubSplit.length; i++) {
							if (lineSubSplit[i].length != 3) throw new RuntimeException("Unreadable wavefront file.");

							int f1 = Integer.parseInt(lineSubSplit[i][0]) - 1;
							int f2 = Integer.parseInt(lineSubSplit[i][1]) - 1;
							int f3 = Integer.parseInt(lineSubSplit[i][2]) - 1;

							//find existing tuple
							for (int tupleIndex = 0; tupleIndex < vertexTuples.size(); tupleIndex++) {
								VertexTuple tuple = vertexTuples.get(tupleIndex);

								if (tuple.equals(f1, f2, f3)) {
									tupleIndices[i] = tupleIndex;
									continue vertexLoop;
								}
							}

							//if no tuple was found
							vertexTuples.add(new VertexTuple(f1, f2, f3));
							tupleIndices[i] = vertexTuples.size() - 1;
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

		return object;
	}
}
