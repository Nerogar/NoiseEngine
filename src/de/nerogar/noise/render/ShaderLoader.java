package de.nerogar.noise.render;

import java.io.*;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

public class ShaderLoader {

	public static Shader loadShader(String vertexShaderFile, String fragmentShaderFile) {
		String vertexShader = readFile(decodeFilename(null, vertexShaderFile));
		String fragmentShader = readFile(decodeFilename(null, fragmentShaderFile));

		Shader shader = new Shader(vertexShaderFile + " " + fragmentShaderFile);
		shader.setVertexShader(vertexShader);
		shader.setFragmentShader(fragmentShader);
		shader.compile();

		return shader;
	}

	public static Shader loadShader(String vertexShaderFile, String geometryShaderFile, String fragmentShaderFile) {
		String vertexShader = readFile(decodeFilename(null, vertexShaderFile));
		String geometryShader = readFile(decodeFilename(null, geometryShaderFile));
		String fragmentShader = readFile(decodeFilename(null, fragmentShaderFile));

		Shader shader = new Shader(vertexShaderFile + " " + fragmentShaderFile);
		shader.setVertexShader(vertexShader);
		shader.setGeometryShader(geometryShader);
		shader.setFragmentShader(fragmentShader);
		shader.compile();

		return shader;
	}

	private static String decodeFilename(String parent, String id) {
		if (id.startsWith("<")) {
			id = id.substring(1, id.indexOf(">"));

			return Noise.RESSOURCE_DIR + "shaders/" + id;
		} else {
			if (parent != null) {
				id = parent + "/" + id;
			}

			return id;
		}
	}

	private static String readFile(String filename) {
		filename = filename.replaceAll("\\\\", "/"); //replace \ with /

		File file = new File(filename);
		String folder = file.getParent();

		StringBuilder text = new StringBuilder();

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(filename));
			String line;
			int lineNumber = 1;

			while ((line = fileReader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("#include ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(9);

					String nextFilename = decodeFilename(folder, line);

					text.append("#line 1\n");
					text.append(readFile(nextFilename));
					text.append("#line " + (lineNumber + 1) + "\n");

					/*if (line.startsWith("<")) {
						line = line.substring(1, line.indexOf(">"));

						text.append(readFile(Noise.RESSOURCE_DIR + "shaders/" + line));
					} else {
						line = folder + "/" + line;

						text.append(readFile(line));
					}*/
				} else {
					text.append(line).append("\n");
				}

				lineNumber++;
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Logger.log(Logger.INFO, "loaded shader: " + filename);

		return text.toString();
	}

}
