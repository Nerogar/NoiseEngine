package de.nerogar.noise.render;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import de.nerogar.noise.util.Logger;

public class Shader {

	private int shaderHandle;

	private int vertexShaderHandle;
	private int geometryShaderHandle;
	private int fragmentShaderHandle;

	private String vertexShader;
	private String geometryShader;
	private String fragmentShader;

	private Map<String, Integer> uniformCache;

	private boolean active;
	private boolean compiled;

	private String name;

	public Shader(String name) {
		this.name = name;

		uniformCache = new HashMap<String, Integer>();
	}

	//---[uniforms]---

	private int getUniformLocation(String name) {
		Integer location = uniformCache.get(name);
		if (location != null) return location;

		location = glGetUniformLocation(shaderHandle, name);
		uniformCache.put(name, location);
		return location;
	}

	private boolean checkUniformActiveState() {
		/*if (!active) {
			Logger.log(Logger.WARNING, "Tried to set uniform while shader was not active!");
		}*/
		return active;
	}

	//float
	public void setUniformf(String name, float[] values) {
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(values.length);
		floatBuffer.put(values);
		floatBuffer.flip();
		setUniformf(name, floatBuffer);
	}

	public void setUniformf(String name, FloatBuffer floatBuffer) {
		if (checkUniformActiveState()) {
			glUniform1fv(getUniformLocation(name), floatBuffer);
		}
	}

	public void setUniform1f(String name, float f0) {
		if (checkUniformActiveState()) {
			glUniform1f(getUniformLocation(name), f0);
		}
	}

	public void setUniform2f(String name, float f0, float f1) {
		if (checkUniformActiveState()) {
			glUniform2f(getUniformLocation(name), f0, f1);
		}
	}

	public void setUniform3f(String name, float f0, float f1, float f2) {
		if (checkUniformActiveState()) {
			glUniform3f(getUniformLocation(name), f0, f1, f2);
		}
	}

	public void setUniform4f(String name, float f0, float f1, float f2, float f3) {
		if (checkUniformActiveState()) {
			glUniform4f(getUniformLocation(name), f0, f1, f2, f3);
		}
	}

	public void setUniformMat2f(String name, FloatBuffer buffer) {
		if (checkUniformActiveState()) {
			glUniformMatrix2fv(getUniformLocation(name), true, buffer);
		}
	}

	public void setUniformMat3f(String name, FloatBuffer buffer) {
		if (checkUniformActiveState()) {
			glUniformMatrix3fv(getUniformLocation(name), true, buffer);
		}
	}

	public void setUniformMat4f(String name, FloatBuffer buffer) {
		if (checkUniformActiveState()) {
			glUniformMatrix4fv(getUniformLocation(name), true, buffer);
		}
	}

	//int
	public void setUniformi(String name, int[] values) {
		if (checkUniformActiveState()) {
			IntBuffer intBuffer = BufferUtils.createIntBuffer(values.length);
			intBuffer.put(values);
			intBuffer.flip();
			setUniformi(name, intBuffer);
		}
	}

	public void setUniformi(String name, IntBuffer intBuffer) {
		if (checkUniformActiveState()) {
			glUniform1iv(getUniformLocation(name), intBuffer);
		}
	}

	public void setUniform1i(String name, int i0) {
		if (checkUniformActiveState()) {
			glUniform1i(getUniformLocation(name), i0);
		}
	}

	public void setUniform2i(String name, int i0, int i1) {
		if (checkUniformActiveState()) {
			glUniform2i(getUniformLocation(name), i0, i1);
		}
	}

	public void setUniform3i(String name, int i0, int i1, int i2) {
		if (checkUniformActiveState()) {
			glUniform3i(getUniformLocation(name), i0, i1, i2);
		}
	}

	public void setUniform4i(String name, int i0, int i1, int i2, int i3) {
		if (checkUniformActiveState()) {
			glUniform4i(getUniformLocation(name), i0, i1, i2, i3);
		}
	}

	//boolean
	public void setUniform1bool(String name, boolean b0) {
		if (checkUniformActiveState()) {
			glUniform1i(getUniformLocation(name), b0 ? 1 : 0);
		}
	}

	//---[end uniforms]---

	public void setVertexShader(String shader) {
		this.vertexShader = shader;
	}

	public void setGeometryShader(String shader) {
		this.geometryShader = shader;
	}

	public void setFragmentShader(String shader) {
		this.fragmentShader = shader;
	}

	public void compile() {
		if (compiled) {
			cleanup();
		}

		shaderHandle = glCreateProgram();

		vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
		if (geometryShader != null) geometryShaderHandle = glCreateShader(GL_GEOMETRY_SHADER);
		fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);

		boolean compileError = false;

		if (!compileError) {
			glShaderSource(vertexShaderHandle, vertexShader);
			glCompileShader(vertexShaderHandle);

			compileError |= getCompileStatus(vertexShaderHandle, "Vertex shader");
		}

		if (geometryShader != null && !compileError) {
			glShaderSource(geometryShaderHandle, geometryShader);
			glCompileShader(geometryShaderHandle);

			compileError |= getCompileStatus(geometryShaderHandle, "Geometry shader");
		}

		if (!compileError) {
			glShaderSource(fragmentShaderHandle, fragmentShader);
			glCompileShader(fragmentShaderHandle);

			compileError |= getCompileStatus(fragmentShaderHandle, "Fragment shader");
		}

		if (!compileError) {
			glAttachShader(shaderHandle, vertexShaderHandle);
			if (geometryShader != null) glAttachShader(shaderHandle, geometryShaderHandle);
			glAttachShader(shaderHandle, fragmentShaderHandle);

			glLinkProgram(shaderHandle);
			if (glGetProgrami(shaderHandle, GL_LINK_STATUS) == GL_FALSE) {
				int errorSize = glGetProgrami(shaderHandle, GL_INFO_LOG_LENGTH);

				Noise.getLogger().log(Logger.ERROR, "Shader program wasn't linked correctly. Error log:\n" + glGetProgramInfoLog(shaderHandle, errorSize));
				compileError = true;
			}
		}

		glDeleteShader(vertexShaderHandle);
		glDeleteShader(geometryShaderHandle);
		glDeleteShader(fragmentShaderHandle);

		if (!compileError) {
			compiled = true;
		} else {
			cleanup();
		}

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.SHADER_COUNT);
		Noise.getResourceProfiler().incrementValue(ResourceProfiler.SHADER_COMPILE_COUNT);
	}

	private boolean getCompileStatus(int shaderHandle, String shaderName) {
		if (glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == GL_FALSE) {
			int errorSize = glGetShaderi(shaderHandle, GL_INFO_LOG_LENGTH);
			Noise.getLogger().log(Logger.ERROR, shaderName + " wasn't able to be compiled correctly. Error log:\n" + glGetShaderInfoLog(shaderHandle, errorSize));
			return true;
		}

		return false;
	}

	public void activate() {
		if (!active && compiled) {
			active = true;
			glUseProgram(shaderHandle);

			Noise.getResourceProfiler().incrementValue(ResourceProfiler.SHADER_BINDS);
		} else if (!compiled) {
			//Logger.log(Logger.DEBUG, "Shader is not compiled. " + toString());
		}
	}

	public void deactivate() {
		if (active) {
			active = false;
			glUseProgram(0);
		}
	}

	public void cleanup() {
		glDeleteProgram(shaderHandle);
		uniformCache.clear();
		compiled = false;

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.SHADER_COUNT);
	}

	@Override
	public String toString() {
		return "[" + shaderHandle + ", " + name + "]";
	}

	@Override
	protected void finalize() throws Throwable {
		if (compiled) Noise.getLogger().log(Logger.WARNING, "Shader not cleaned up. " + toString());
	}

}
