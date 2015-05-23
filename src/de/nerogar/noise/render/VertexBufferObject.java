package de.nerogar.noise.render;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import de.nerogar.noise.log.Logger;

public class VertexBufferObject {
	private int vboHandle;
	private HashMap<Long, Integer> glContextVaoHandles;

	private int vertices;
	private int totalComponents;
	private int[] componentCounts;
	private int[] incrementalComponentCounts;

	private boolean deleted;

	/**
	 * @param componentCounts an array containing all component counts
	 * @param attributes arays containing all components to use for this VBO
	 * 
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObject(int[] componentCounts, float[]... attributes) {
		if (componentCounts.length != attributes.length) throw new ArrayIndexOutOfBoundsException();

		glContextVaoHandles = new HashMap<Long, Integer>();

		this.componentCounts = componentCounts;

		//create Buffer
		incrementalComponentCounts = new int[componentCounts.length];
		totalComponents = 0;

		for (int i = 0; i < componentCounts.length; i++) {
			incrementalComponentCounts[i] = totalComponents;
			totalComponents += componentCounts[i];
		}

		if (attributes.length > 0) {
			vertices = attributes[0].length / componentCounts[0];
		} else {
			vertices = 0;
		}

		float[] attribArray = new float[totalComponents * vertices];

		for (int attrib = 0; attrib < attributes.length; attrib++) {
			for (int vertex = 0; vertex < vertices; vertex++) {
				for (int component = 0; component < componentCounts[attrib]; component++) {
					attribArray[vertex * totalComponents + incrementalComponentCounts[attrib] + component] = attributes[attrib][component + vertex * componentCounts[attrib]];
				}
			}
		}

		FloatBuffer buffer = BufferUtils.createFloatBuffer(attribArray.length);
		buffer.put(attribArray);
		buffer.flip();

		//init VAO
		initVAO(buffer);
	}

	private int initVAO(FloatBuffer vboBuffer) {
		int vaoHandle = glGenVertexArrays();
		glContextVaoHandles.put(glfwGetCurrentContext(), vaoHandle);

		glBindVertexArray(vaoHandle);

		if (vboBuffer != null) {
			vboHandle = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
			glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
		} else {
			glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
		}

		for (int i = 0; i < componentCounts.length; i++) {
			glVertexAttribPointer(i, componentCounts[i], GL_FLOAT, false, totalComponents * Float.BYTES, incrementalComponentCounts[i] * Float.BYTES); //pos

			glEnableVertexAttribArray(i);
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glBindVertexArray(0);
		return vaoHandle;
	}

	public void render() {
		Integer vaoHandle = glContextVaoHandles.get(glfwGetCurrentContext());
		if (vaoHandle == null) vaoHandle = initVAO(null);

		glBindVertexArray(vaoHandle);
		glDrawArrays(GL_TRIANGLES, 0, vertices);
		glBindVertexArray(0);
	}

	public void cleanup() {
		long currentContext = glfwGetCurrentContext();
		glDeleteBuffers(vboHandle);

		for (long context : glContextVaoHandles.keySet()) {
			glfwMakeContextCurrent(context);

			glDeleteVertexArrays(glContextVaoHandles.get(context));
		}

		glfwMakeContextCurrent(currentContext);

		deleted = true;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!deleted) Logger.log(Logger.WARNING, "VBO not cleaned up. pointer: " + vboHandle);
	}

}
