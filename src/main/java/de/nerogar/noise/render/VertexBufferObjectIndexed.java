package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import org.lwjgl.system.jemalloc.JEmalloc;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VertexBufferObjectIndexed extends VertexBufferObject {

	private int vboHandle;
	private int indexBufferHandle;

	private int   indexCount;
	private int[] componentCounts;
	private int[] componentCountsPrefixSum;
	private int   totalComponentCount;

	/**
	 * @param componentCounts an array containing all component counts
	 * @param indexCount      number of vertices specified in indexArray
	 * @param vertexCount     number of vertices specified in attributes
	 * @param indexArray      an array containing the index data
	 * @param attributes      arrays containing all components to use for this VBO
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectIndexed(int[] componentCounts, int indexCount, int vertexCount, int[] indexArray, Object... attributes) {
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
	public VertexBufferObjectIndexed(int renderType, int[] componentCounts, int indexCount, int vertexCount, int[] indexArray, Object... attributes) {
		super(renderType);
		if (componentCounts.length != attributes.length) throw new ArrayIndexOutOfBoundsException();

		this.componentCounts = componentCounts;
		this.componentCountsPrefixSum = new int[componentCounts.length];
		this.indexCount = indexCount;

		int totalComponentCount = 0;
		for (int i = 0; i < componentCounts.length; i++) {
			componentCountsPrefixSum[i] = totalComponentCount;
			totalComponentCount += componentCounts[i];
		}
		this.totalComponentCount = totalComponentCount;

		// allocate buffers
		vboHandle = glGenBuffers();
		indexBufferHandle = glGenBuffers();

		// init vbo
		setData(indexCount, vertexCount, indexArray, attributes);
		initVAO(vboHandle, indexBufferHandle);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_COUNT);
	}

	/**
	 * @param indexCount  number of vertices specified in indexArray
	 * @param vertexCount number of vertices specified in attributes
	 * @param indexArray  an array containing the index data
	 * @param attributes  arrays containing all components to use for this VBO
	 */
	public void setData(int indexCount, int vertexCount, int[] indexArray, Object... attributes) {
		ByteBuffer buffer = JEmalloc.je_malloc((long) totalComponentCount * vertexCount * Float.BYTES);
		createInterleavedBuffer(buffer, componentCounts, vertexCount, attributes);
		buffer.flip();

		IntBuffer indexBuffer = JEmalloc.je_malloc((long) indexCount * Integer.BYTES).asIntBuffer();
		indexBuffer.put(indexArray, 0, indexCount);
		indexBuffer.flip();

		initVBO(buffer, indexBuffer);
		JEmalloc.je_free(buffer);
		JEmalloc.je_free(indexBuffer);
	}

	@Override
	public int getBufferName() {
		return vboHandle;
	}

	@Override
	public void render() {
		Integer vaoHandle = glContextVaoHandles.get(GLWindow.getCurrentContext());
		if (vaoHandle == null) vaoHandle = initVAO(vboHandle, indexBufferHandle);

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

	private static void createInterleavedBuffer(ByteBuffer byteBuffer, int[] componentCounts, int vertexCount, Object... attributes) {
		int[] types = new int[attributes.length];
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] instanceof int[]) {
				types[i] = 0x04;
			} else if (attributes[i] instanceof float[]) {
				types[i] = 0x14;
			}
		}

		for (int vertex = 0; vertex < vertexCount; vertex++) {
			for (int attrib = 0; attrib < attributes.length; attrib++) {
				for (int component = 0; component < componentCounts[attrib]; component++) {
					switch (types[attrib]) {
						case 0x04 -> byteBuffer.putInt(((int[]) attributes[attrib])[component + vertex * componentCounts[attrib]]);
						case 0x14 -> byteBuffer.putFloat(((float[]) attributes[attrib])[component + vertex * componentCounts[attrib]]);
					}
				}
			}
		}
	}

	private void initVBO(ByteBuffer vboBuffer, IntBuffer indexBuffer) {
		glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
		glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_UPLOAD_COUNT);
		Noise.getResourceProfiler().addValue(ResourceProfiler.VBO_UPLOAD_SIZE, vboBuffer.remaining() * Float.BYTES);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_UPLOAD_COUNT);
		Noise.getResourceProfiler().addValue(ResourceProfiler.VBO_UPLOAD_SIZE, indexBuffer.remaining() * Integer.BYTES);
	}

	private int initVAO(int vboHandle, int indexBufferHandle) {
		int vaoHandle = glGenVertexArrays();
		glContextVaoHandles.put(GLWindow.getCurrentContext(), vaoHandle);

		glBindVertexArray(vaoHandle);

		// vertex data
		glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
		for (int i = 0; i < componentCounts.length; i++) {
			glVertexAttribPointer(i, componentCounts[i], GL_FLOAT, false, totalComponentCount * Float.BYTES, (long) componentCountsPrefixSum[i] * Float.BYTES);
			glEnableVertexAttribArray(i);
		}
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// index data
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle);

		glBindVertexArray(0);

		return vaoHandle;
	}

}
