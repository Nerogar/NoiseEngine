package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.debug.ResourceProfiler;
import org.lwjgl.system.jemalloc.JEmalloc;

import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VertexBufferObjectStandard extends VertexBufferObject {

	private int vboHandle;

	private int   vertexCount;
	private int   vertexCountCap = -1;
	private int   totalComponents;
	private int[] componentCounts;
	private int[] incrementalComponentCounts;

	/**
	 * @param componentCounts an array containing all component counts
	 * @param attributes      arrays containing all components to use for this VBO
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectStandard(int[] componentCounts, float[]... attributes) {
		this(TRIANGLES, componentCounts, attributes);
	}

	/**
	 * @param renderType      type of rendered primitives. Either {@link VertexBufferObject#POINTS POINTS},
	 *                        {@link VertexBufferObject#TRIANGLES TRIANGLES} or {@link VertexBufferObject#LINES LINES}
	 * @param componentCounts an array containing all component counts
	 * @param attributes      arrays containing all components to use for this VBO
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectStandard(int renderType, int[] componentCounts, float[]... attributes) {
		this.renderType = renderType;

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
			vertexCount = attributes[0].length / componentCounts[0];
		} else {
			vertexCount = 0;
		}

		float[] attribArray = new float[totalComponents * vertexCount];

		for (int attrib = 0; attrib < attributes.length; attrib++) {
			for (int vertex = 0; vertex < vertexCount; vertex++) {
				for (int component = 0; component < componentCounts[attrib]; component++) {
					attribArray[vertex * totalComponents + incrementalComponentCounts[attrib] + component] = attributes[attrib][component + vertex * componentCounts[attrib]];
				}
			}
		}

		// allocate the buffer and fill it
		FloatBuffer buffer = JEmalloc.je_malloc(attribArray.length * Float.BYTES).asFloatBuffer();
		buffer.put(attribArray);
		buffer.flip();

		initVAO(buffer);

		// free the buffer again
		JEmalloc.je_free(buffer);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_COUNT);
	}

	private int initVAO(FloatBuffer vboBuffer) {
		int vaoHandle = glGenVertexArrays();
		glContextVaoHandles.put(GLWindow.getCurrentContext(), vaoHandle);

		glBindVertexArray(vaoHandle);

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

		glBindVertexArray(0);

		if (vboBuffer != null) {
			Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_UPLOAD_COUNT);
			Noise.getResourceProfiler().addValue(ResourceProfiler.VBO_UPLOAD_SIZE, vboBuffer.remaining() * Float.BYTES);
		}

		return vaoHandle;
	}

	@Override
	public int getBufferName() {
		return vboHandle;
	}

	public void setVertexCountCap(int vertexCountCap) {
		if (vertexCountCap <= vertexCount) {
			this.vertexCountCap = vertexCountCap;
		}
	}

	@Override
	public void render() {
		Integer vaoHandle = glContextVaoHandles.get(GLWindow.getCurrentContext());
		if (vaoHandle == null) vaoHandle = initVAO(null);

		glBindVertexArray(vaoHandle);
		if (vertexCountCap < 0) {
			glDrawArrays(renderType, 0, vertexCount);
		} else {
			glDrawArrays(renderType, 0, vertexCountCap);
		}
		glBindVertexArray(0);

		Noise.getResourceProfiler().incrementValue(ResourceProfiler.VBO_CALLS);
	}

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;

		long currentContext = GLWindow.getCurrentContext();
		glDeleteBuffers(vboHandle);

		for (long glContext : glContextVaoHandles.keySet()) {
			GLWindow.makeContextCurrent(glContext);

			glDeleteVertexArrays(glContextVaoHandles.get(glContext));
		}

		GLWindow.makeContextCurrent(currentContext);

		Noise.getResourceProfiler().decrementValue(ResourceProfiler.VBO_COUNT);

		return true;
	}

}
