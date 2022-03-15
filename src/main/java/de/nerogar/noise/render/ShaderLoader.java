package de.nerogar.noise.render;

import de.nerogar.noise.file.*;
import de.nerogar.noise.util.ProgramLoader;

import java.util.Map;

/**
 * Utility class for easy shader loading. Special syntax can be used as defined in {@link ProgramLoader ProgramLoader}.
 */
public class ShaderLoader {

	/**
	 * Loads a shader from files.
	 * All file paths are {@link ProgramLoader file IDs}.
	 *
	 * @param vertexShaderFile   the vertex shader file
	 * @param fragmentShaderFile the fragment shader file
	 * @return the new shader
	 */
	public static Shader loadShader(ResourceDescriptor vertexShaderFile, ResourceDescriptor fragmentShaderFile) {
		return loadShader(vertexShaderFile, fragmentShaderFile, (Map<String, String>) null);
	}

	/**
	 * Loads a shader from files.
	 * All file paths are {@link ProgramLoader file IDs}.
	 * More info on parameters {@link ProgramLoader here}.
	 *
	 * @param vertexShaderFile   the vertex shader file
	 * @param fragmentShaderFile the fragment shader file
	 * @param parameters         a map containing all parameters
	 * @return the new shader
	 */
	public static Shader loadShader(ResourceDescriptor vertexShaderFile, ResourceDescriptor fragmentShaderFile, Map<String, String> parameters) {
		String vertexShader = ProgramLoader.readFile(vertexShaderFile, parameters);
		String fragmentShader = ProgramLoader.readFile(fragmentShaderFile, parameters);

		Shader shader = new Shader(vertexShaderFile.getFilename() + " " + fragmentShaderFile.getFilename());
		shader.setVertexShader(vertexShader);
		shader.setFragmentShader(fragmentShader);
		shader.compile();

		return shader;
	}

	/**
	 * Loads a shader from files.
	 * All file paths are {@link ProgramLoader file IDs}.
	 *
	 * @param vertexShaderFile   the vertex shader file
	 * @param geometryShaderFile the geometry shader file
	 * @param fragmentShaderFile the fragment shader file
	 * @return the new shader
	 */
	public static Shader loadShader(ResourceDescriptor vertexShaderFile, ResourceDescriptor geometryShaderFile, ResourceDescriptor fragmentShaderFile) {
		return loadShader(vertexShaderFile, geometryShaderFile, fragmentShaderFile, (Map<String, String>) null);
	}

	/**
	 * Loads a shader from files.
	 * All file paths are {@link ProgramLoader file IDs}.
	 * More info on parameters {@link ProgramLoader here}.
	 *
	 * @param vertexShaderFile   the vertex shader file
	 * @param geometryShaderFile the geometry shader file
	 * @param fragmentShaderFile the fragment shader file
	 * @param parameters         a map containing all parameters
	 * @return the new shader
	 */
	public static Shader loadShader(ResourceDescriptor vertexShaderFile, ResourceDescriptor geometryShaderFile, ResourceDescriptor fragmentShaderFile, Map<String, String> parameters) {
		String vertexShader = ProgramLoader.readFile(vertexShaderFile, parameters);
		String geometryShader = ProgramLoader.readFile(geometryShaderFile, parameters);
		String fragmentShader = ProgramLoader.readFile(fragmentShaderFile, parameters);

		Shader shader = new Shader(vertexShaderFile + " " + fragmentShaderFile);
		shader.setVertexShader(vertexShader);
		shader.setGeometryShader(geometryShader);
		shader.setFragmentShader(fragmentShader);
		shader.compile();

		return shader;
	}

}
