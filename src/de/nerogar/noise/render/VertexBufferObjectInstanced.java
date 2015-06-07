package de.nerogar.noise.render;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.*;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import de.nerogar.noise.util.Logger;

public class VertexBufferObjectInstanced extends VertexBufferObject {

	private int vboHandle;
	private int indexBufferHandle;
	private int instanceBufferHandle;

	private int vertexCount;
	private int indexCount;
	private int totalComponents;
	private int[] componentCounts;
	private int[] incrementalComponentCounts;

	private int instanceCount;
	private int totalComponentsInstance;
	private int[] componentCountsInstance;
	private int[] incrementalComponentCountsInstance;
	
	private float[] attribArrayInstance;
	private ByteBuffer instanceBuffer;

	protected HashMap<Long, Boolean> glContextInstanceDataDirty;

	/**
	 * @param componentCounts an array containing all component counts
	 * @param indexArary an array containing the index data
	 * @param attributes arays containing all components to use for this VBO
	 * 
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectInstanced(int[] componentCounts, int[] indexArary, float[]... attributes) {
		this(TRIANGLES, componentCounts, indexArary, attributes);
	}

	/**
	 * @param renderType type of rendered primitives. Either {@link VertexBufferObject#POINTS POINTS},
	 * {@link VertexBufferObject#TRIANGLES TRIANGLES} or {@link VertexBufferObject#LINES LINES}
	 * @param componentCounts an array containing all component counts
	 * @param indexArary an array containing the index data
	 * @param attributes arays containing all components to use for this VBO
	 * 
	 * @throws ArrayIndexOutOfBoundsException if componentCounts.length does not equal the amount of attribute arrays
	 */
	public VertexBufferObjectInstanced(int renderType, int[] componentCounts, int[] indexArary, float[]... attributes) {
		if (componentCounts.length != attributes.length) throw new ArrayIndexOutOfBoundsException();

		this.renderType = renderType;
		glContextVaoHandles = new HashMap<Long, Integer>();
		glContextInstanceDataDirty = new HashMap<Long, Boolean>();
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
		indexCount = indexArary.length;

		float[] attribArray = new float[totalComponents * vertexCount];

		for (int attrib = 0; attrib < attributes.length; attrib++) {
			for (int vertex = 0; vertex < vertexCount; vertex++) {
				for (int component = 0; component < componentCounts[attrib]; component++) {
					attribArray[vertex * totalComponents + incrementalComponentCounts[attrib] + component] = attributes[attrib][component + vertex * componentCounts[attrib]];
				}
			}
		}

		FloatBuffer buffer = BufferUtils.createFloatBuffer(attribArray.length);
		buffer.put(attribArray);
		buffer.flip();

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indexArary.length);
		indexBuffer.put(indexArary);
		indexBuffer.flip();

		initVAO(buffer, indexBuffer, null);
	}

	private int initVAO(FloatBuffer vboBuffer, IntBuffer indexBuffer, ByteBuffer instanceBuffer) {
		long currentContext = glfwGetCurrentContext();
		Integer oldVaoHandle = glContextVaoHandles.get(currentContext);
		if (oldVaoHandle != null) glDeleteVertexArrays(oldVaoHandle);

		int vaoHandle = glGenVertexArrays();
		glContextVaoHandles.put(currentContext, vaoHandle);

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

		//index data
		if (indexBuffer != null) {
			indexBufferHandle = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		} else {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle);
		}

		//instance data
		if (instanceBufferHandle == 0) instanceBufferHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, instanceBufferHandle);
		if (instanceBuffer != null) glBufferData(GL_ARRAY_BUFFER, instanceCount * totalComponentsInstance * Float.BYTES, instanceBuffer, GL_STATIC_DRAW);

		if (componentCountsInstance != null) {
			for (int i = 0; i < componentCountsInstance.length; i++) {
				glEnableVertexAttribArray(i + componentCounts.length);
				glVertexAttribPointer(i + componentCounts.length, componentCountsInstance[i], GL_FLOAT, false, totalComponentsInstance * Float.BYTES, incrementalComponentCountsInstance[i] * Float.BYTES);
				glVertexAttribDivisor(i + componentCounts.length, 1);
			}
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glBindVertexArray(0);
		glContextInstanceDataDirty.put(currentContext, false);
		return vaoHandle;

	}

	/**
	 * Updates the instance data of this VertexBuffer. ComponentCounts should contain only numbers from 1 to 4.
	 * If you want to add 4x4 matrices, you have to specify 4 arrays, each containing a single collumn of the matrix
	 * 
	 * @param instanceCount number of instances to render
	 * @param componentCountsInstance an array containing all component counts
	 * @param attributesInstance arays containing all components to use for this VBO as instance data
	 */
	public void setInstanceData(int instanceCount, int[] componentCountsInstance, float[]... attributesInstance) {
		this.componentCountsInstance = componentCountsInstance;
		this.instanceCount = instanceCount;

		Integer vaoHandle = glContextVaoHandles.get(glfwGetCurrentContext());

		glBindVertexArray(vaoHandle);

		//create instance buffer
		incrementalComponentCountsInstance = new int[componentCountsInstance.length];
		totalComponentsInstance = 0;

		for (int i = 0; i < componentCountsInstance.length; i++) {
			incrementalComponentCountsInstance[i] = totalComponentsInstance;
			totalComponentsInstance += componentCountsInstance[i];
		}

		if (attributesInstance.length > 0) {
			instanceCount = attributesInstance[0].length / componentCountsInstance[0];
		} else {
			instanceCount = 0;
		}

		if (attribArrayInstance == null || attribArrayInstance.length < totalComponentsInstance * instanceCount){
			attribArrayInstance = new float[totalComponentsInstance * instanceCount];
			instanceBuffer = BufferUtils.createByteBuffer(attribArrayInstance.length * Float.BYTES);
		}

		for (int attribInstance = 0; attribInstance < attributesInstance.length; attribInstance++) {
			for (int instance = 0; instance < instanceCount; instance++) {
				for (int componentInstance = 0; componentInstance < componentCountsInstance[attribInstance]; componentInstance++) {
					int attribIndexTarget = instance * totalComponentsInstance + incrementalComponentCountsInstance[attribInstance] + componentInstance;
					int attribIndexSource = componentInstance + instance * componentCountsInstance[attribInstance];

					attribArrayInstance[attribIndexTarget] = attributesInstance[attribInstance][attribIndexSource];
				}
			}
		}

		instanceBuffer.clear();
		instanceBuffer.asFloatBuffer().put(attribArrayInstance);

		for (Long l : glContextInstanceDataDirty.keySet()) {
			glContextInstanceDataDirty.put(l, true);
		}

		initVAO(null, null, instanceBuffer);

		glBindVertexArray(0);
	}

	@Override
	public void render() {
		long currentContext = glfwGetCurrentContext();
		Integer vaoHandle = glContextVaoHandles.get(currentContext);
		Boolean instanceDataDirty = glContextInstanceDataDirty.get(currentContext);

		if (vaoHandle == null || instanceDataDirty == null || instanceDataDirty) {
			vaoHandle = initVAO(null, null, null);
		}

		glBindVertexArray(vaoHandle);
		glDrawElementsInstanced(renderType, indexCount, GL_UNSIGNED_INT, 0, instanceCount);
		glBindVertexArray(0);

	}

	@Override
	public void cleanup() {
		long currentContext = glfwGetCurrentContext();
		glDeleteBuffers(vboHandle);
		glDeleteBuffers(indexBufferHandle);
		glDeleteBuffers(instanceBufferHandle);

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
