package de.nerogar.noise.render;

import java.io.*;
import java.util.Map;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

public class ShaderLoader {

	private static final Map<String, String> EMPTY_PARAMETERS = null;

	public static Shader loadShader(String vertexShaderFile, String fragmentShaderFile) {
		return loadShader(vertexShaderFile, fragmentShaderFile, EMPTY_PARAMETERS);
	}

	public static Shader loadShader(String vertexShaderFile, String fragmentShaderFile, Map<String, String> parameters) {
		String vertexShader = readFile(decodeFilename(null, vertexShaderFile), parameters);
		String fragmentShader = readFile(decodeFilename(null, fragmentShaderFile), parameters);

		Shader shader = new Shader(vertexShaderFile + " " + fragmentShaderFile);
		shader.setVertexShader(vertexShader);
		shader.setFragmentShader(fragmentShader);
		shader.compile();

		return shader;
	}

	public static Shader loadShader(String vertexShaderFile, String geometryShaderFile, String fragmentShaderFile) {
		return loadShader(vertexShaderFile, geometryShaderFile, fragmentShaderFile, EMPTY_PARAMETERS);
	}

	public static Shader loadShader(String vertexShaderFile, String geometryShaderFile, String fragmentShaderFile, Map<String, String> parameters) {
		String vertexShader = readFile(decodeFilename(null, vertexShaderFile), parameters);
		String geometryShader = readFile(decodeFilename(null, geometryShaderFile), parameters);
		String fragmentShader = readFile(decodeFilename(null, fragmentShaderFile), parameters);

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

	private static String readFile(String filename, Map<String, String> parameters) {
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
					text.append(readFile(nextFilename, parameters));
					text.append("#line " + (lineNumber + 1) + "\n");
				} else if (line.startsWith("#parameter ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(11);

					String parameter = readFile(decodeFilename(folder, parameters.get(line).trim()), parameters);

					text.append("#line 1\n");
					text.append(parameter);
					text.append("#line " + (lineNumber + 1) + "\n");
				} else {
					text.append(line).append("\n");
				}

				lineNumber++;
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		text.append("\n");

		Logger.log(Logger.INFO, "loaded shader: " + filename);

		return text.toString();
	}

}
