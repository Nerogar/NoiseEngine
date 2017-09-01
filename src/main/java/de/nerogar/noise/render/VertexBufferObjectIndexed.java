package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import org.lwjgl.system.jemalloc.JEmalloc;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VertexBufferObjectIndexed extends VertexBufferObject {

	private int vboHandle;
	private int indexBufferHandle;

	private int   indexCount;
	private int   totalComponents;
	private int[] componentCounts;
	private int[] incrementalComponentCounts;

	/**
	 * @param componentCounts an array containing all component counts
	 * @param indexCount      number of vertices specified in indexArray
	 * @param vertexCount     number of vertices specified in attributes
	 * @param indexArray      an array containing the index data
	 * @param attributes      arrays containing all components to use for this VBO
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectIndexed(int[] componentCounts, int indexCount, int vertexCount, int[] indexArray, float[]... attributes) {
		this(TRIANGLES, componentCounts, indexCount, vertexCount, indexArray, attributes);
	}

	/**
	 * @param renderType      type of rendered primitives. Either {@link VertexBufferObject#POINTS POINTS},
	 *                        {@link VertexBufferObject#TRIANGLES TRIANGLES} or {@link VertexBufferObject#LINES LINES}
	 * @param componentCounts an array containing all component counts
	 * @param indexCount      number of vertices specified in indexArray
	 * @param vertexCount     number of vertices specified in attributes
	 * @param indexArray      an array containing the index data
	 * @param attributes      arrays containing all components to use for this VBO
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectIndexed(int renderType, int[] componentCounts, int indexCount, int vertexCount, int[] indexArray, float[]... attributes) {
		if (componentCounts.length != attributes.length) throw new ArrayIndexOutOfBoundsException();

		this.renderType = renderType;
		this.componentCounts = componentCounts;
		this.indexCount = indexCount;

		glContextVaoHandles = new HashMap<Long, Integer>();

		//create Buffer
		incrementalComponentCounts = new int[componentCounts.length];
		totalComponents = 0;

		for (int i = 0; i < componentCounts.length; i++) {
			incrementalComponentCounts[i] = totalComponents;
			totalComponents += componentCounts[i];
		}

		float[] attribArray = new float[totalComponents * vertexCount];

		for (int attrib = 0; attrib < attributes.length; attrib++) {
			for (int vertex = 0; vertex < vertexCount; vertex++) {
				for (int component = 0; component < componentCounts[attrib]; component++) {
					attribArray[vertex * totalComponents + incrementalComponentCounts[attrib] + component] = attributes[attrib][component + vertex * componentCounts[attrib]];
				}
			}
		}

		// allocate buffers and fill them
		FloatBuffer buffer = JEmalloc.je_malloc(totalComponents * vertexCount * Float.BYTES).asFloatBuffer();
		buffer.put(attribArray, 0, totalComponents * vertexCount);
		buffer.flip();

		IntBuffer indexBuffer = JEmalloc.je_malloc(indexCount * Integer.BYTES).asIntBuffer();
		indexBuffer.put(indexArray, 0, indexCount);
		indexBuffer.flip();

		initVAO(buffer, indexBuffer);

		// free buffers again
		JEmalloc.je_free(buffer);
		JEmalloc.je_free(indexBuffer);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_COUNT);
	}

	private int initVAO(FloatBuffer vboBuffer, IntBuffer indexBuffer) {
		int vaoHandle = glGenVertexArrays();
		glContextVaoHandles.put(GLWindow.getCurrentContext(), vaoHandle);

		glBindVertexArray(vaoHandle);

		//vertex data
		if (vboBuffer != null) {
			vboHandle = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
			glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
		} else {
			glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
		}

		for (int i = 0; i < componentCounts.length; i++) {
			glVertexAttribPointer(i, componentCounts[i], GL_FLOAT, false, totalComponents * Float.BYTES, incrementalComponentCounts[i] * Float.BYTES);

			glEnableVertexAttribArray(i);
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		//index data
		if (indexBuffer != null) {
			indexBufferHandle = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		} else {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle);
		}

		glBindVertexArray(0);

		if (vboBuffer != null) {
			Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_UPLOAD_COUNT);
			Noise.getResourceProfiler().addValue(ResourceProfiler.VBO_UPLOAD_SIZE, vboBuffer.remaining() * Float.BYTES);
		}
		if (indexBuffer != null) {
			Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_UPLOAD_COUNT);
			Noise.getResourceProfiler().addValue(ResourceProfiler.VBO_UPLOAD_SIZE, indexBuffer.remaining() * Integer.BYTES);
		}

		return vaoHandle;
	}

	@Override
	public int getBufferName() {
		return vboHandle;
	}

	@Override
	public void render() {
		Integer vaoHandle = glContextVaoHandles.get(GLWindow.getCurrentContext());
		if (vaoHandle == null) vaoHandle = initVAO(null, null);

		glBindVertexArray(vaoHandle);
		glDrawElements(renderType, indexCount, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_CALLS);
	}

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;

		long currentContext = GLWindow.getCurrentContext();
		glDeleteBuffers(vboHandle);
		glDeleteBuffers(indexBufferHandle);

		for (long glContext : glContextVaoHandles.keySet()) {
			GLWindow.makeContextCurrent(glContext);

			glDeleteVertexArrays(glContextVaoHandles.get(glContext));
		}

		GLWindow.makeContextCurrent(currentContext);

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.VBO_COUNT);

		return true;
	}

}
