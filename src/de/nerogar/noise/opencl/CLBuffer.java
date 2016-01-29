package de.nerogar.noise.opencl;

import static org.lwjgl.opencl.CL10.*;

import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opencl.CL10GL;

import de.nerogar.noise.render.VertexBufferObject;

public class CLBuffer {

	private static final String ERROR_LOCATION = "CLBuffer";
	private IntBuffer errorCode;

	private CLContext clContext;

	private long bufferPointer;
	private boolean isGLBuffer;

	private ByteBuffer bufferData;

	public CLBuffer(CLContext clContext, VertexBufferObject vbo) {
		if (!clContext.isContextGLShared()) throw new IllegalStateException("Can not create CL buffer from vbo in a non shared context.");

		errorCode = BufferUtils.createIntBuffer(1);

		long bufferPointer = CL10GL.clCreateFromGLBuffer(clContext.getCLContext(), CL_MEM_READ_WRITE, vbo.getBufferName(), errorCode);
		CLContext.checkCLError(errorCode, ERROR_LOCATION);

		this.bufferPointer = bufferPointer;
		this.isGLBuffer = true;
	}

	public CLBuffer(CLContext clContext, byte[] data, boolean read, boolean write) {
		initBuffers(data.length * Byte.BYTES);

		bufferData.put(data);

		initCLBuffer(clContext, read, write);
	}

	public CLBuffer(CLContext clContext, short[] data, boolean read, boolean write) {
		initBuffers(data.length * Short.BYTES);

		ShortBuffer shortBuffer = bufferData.asShortBuffer();
		shortBuffer.put(data);

		initCLBuffer(clContext, read, write);
	}

	public CLBuffer(CLContext clContext, int[] data, boolean read, boolean write) {
		initBuffers(data.length * Integer.BYTES);

		IntBuffer intBuffer = bufferData.asIntBuffer();
		intBuffer.put(data);

		initCLBuffer(clContext, read, write);
	}

	public CLBuffer(CLContext clContext, long[] data, boolean read, boolean write) {
		initBuffers(data.length * Long.BYTES);

		LongBuffer longBuffer = bufferData.asLongBuffer();
		longBuffer.put(data);

		initCLBuffer(clContext, read, write);
	}

	public CLBuffer(CLContext clContext, float[] data, boolean read, boolean write) {
		initBuffers(data.length * Float.BYTES);

		FloatBuffer floatBuffer = bufferData.asFloatBuffer();
		floatBuffer.put(data);

		initCLBuffer(clContext, read, write);
	}

	public CLBuffer(CLContext clContext, double[] data, boolean read, boolean write) {
		initBuffers(data.length * Double.BYTES);

		DoubleBuffer doubleBuffer = bufferData.asDoubleBuffer();
		doubleBuffer.put(data);

		initCLBuffer(clContext, read, write);
	}

	private void initBuffers(int dataBufferSize) {
		errorCode = BufferUtils.createIntBuffer(1);

		bufferData = BufferUtils.createByteBuffer(dataBufferSize);
	}

	private void initCLBuffer(CLContext clContext, boolean read, boolean write) {
		this.clContext = clContext;

		int readWriteFlags = 0;
		if (read && write) {
			readWriteFlags = CL_MEM_READ_WRITE;
		} else if (read) {
			readWriteFlags = CL_MEM_READ_ONLY;
		} else if (write) {
			readWriteFlags = CL_MEM_WRITE_ONLY;
		}
		readWriteFlags |= CL_MEM_COPY_HOST_PTR;

		long bufferPointer = clCreateBuffer(clContext.getCLContext(), readWriteFlags, bufferData, errorCode);

		CLContext.checkCLError(errorCode, ERROR_LOCATION);

		this.bufferPointer = bufferPointer;
		isGLBuffer = false;
	}

	public void enqueueUpdateBuffer(byte[] data, boolean block) {
		bufferData.rewind();
		bufferData.put(data);
		enqueueUpdateBuffer(block);
	}

	public void enqueueUpdateBuffer(short[] data, boolean block) {
		bufferData.rewind();
		bufferData.asShortBuffer().put(data);
		enqueueUpdateBuffer(block);
	}

	public void enqueueUpdateBuffer(int[] data, boolean block) {
		bufferData.rewind();
		bufferData.asIntBuffer().put(data);
		enqueueUpdateBuffer(block);
	}

	public void enqueueUpdateBuffer(long[] data, boolean block) {
		bufferData.rewind();
		bufferData.asLongBuffer().put(data);
		enqueueUpdateBuffer(block);
	}

	public void enqueueUpdateBuffer(float[] data, boolean block) {
		bufferData.rewind();
		bufferData.asFloatBuffer().put(data);
		enqueueUpdateBuffer(block);
	}

	public void enqueueUpdateBuffer(double[] data, boolean block) {
		bufferData.rewind();
		bufferData.asDoubleBuffer().put(data);
		enqueueUpdateBuffer(block);
	}

	private void enqueueUpdateBuffer(boolean block) {
		clEnqueueWriteBuffer(clContext.getCLCommandQueue(), bufferPointer, block ? CL_TRUE : CL_FALSE, 0, bufferData, null, null);
	}

	public void enqueueRead(boolean block) {
		clEnqueueReadBuffer(clContext.getCLCommandQueue(), bufferPointer, block ? CL_TRUE : CL_FALSE, 0, bufferData, null, null);
	}

	public byte[] getByteData() {
		byte[] data = new byte[bufferData.capacity()];
		bufferData.get(data);
		return data;
	}

	public short[] getShortData() {
		short[] data = new short[bufferData.capacity() / Short.BYTES];
		bufferData.asShortBuffer().get(data);
		return data;
	}

	public int[] getIntData() {
		int[] data = new int[bufferData.capacity() / Integer.BYTES];
		bufferData.asIntBuffer().get(data);
		return data;
	}

	public long[] getLongData() {
		long[] data = new long[bufferData.capacity() / Long.BYTES];
		bufferData.asLongBuffer().get(data);
		return data;
	}

	public float[] getFloatData() {
		float[] data = new float[bufferData.capacity() / Float.BYTES];
		bufferData.asFloatBuffer().get(data);
		return data;
	}

	public double[] getDouleData() {
		double[] data = new double[bufferData.capacity() / Double.BYTES];
		bufferData.asDoubleBuffer().get(data);
		return data;
	}

	public boolean isGLBuffer() {
		return isGLBuffer;
	}

	public long getBufferPointer() {
		return bufferPointer;
	}

}
