package de.nerogar.noise.opencl;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10GL.clEnqueueAcquireGLObjects;
import static org.lwjgl.opengl.GL11.glFinish;

public class CLKernel {

	private static final String    ERROR_LOCATION = "CLKernel";
	private              IntBuffer errorCode;

	private CLProgram clProgram;

	private long clKernel;

	private int           workSizeDimensions;
	private PointerBuffer globalWorkSizeBuffer;
	private PointerBuffer localWorkSizeBuffer;

	private ArrayList<CLBuffer> glBuffers;

	public CLKernel(CLProgram clProgram, String kernelName, int... workSize) {
		this.clProgram = clProgram;

		errorCode = BufferUtils.createIntBuffer(1);

		if (clProgram.getCLContext().isContextGLShared()) glBuffers = new ArrayList<>();

		clKernel = clCreateKernel(clProgram.getCLProgram(), kernelName, errorCode);

		workSizeDimensions = workSize.length;
		globalWorkSizeBuffer = BufferUtils.createPointerBuffer(workSizeDimensions);
		for (int i = 0; i < workSizeDimensions; i++) {
			globalWorkSizeBuffer.put(workSize[i]);
		}
		globalWorkSizeBuffer.flip();

		CLContext.checkCLError(errorCode, ERROR_LOCATION);
	}

	public void setLocalWorkSize(int... localWorkSize) {
		if (workSizeDimensions != localWorkSize.length) throw new IllegalArgumentException("global and local work size must have the same dimensionality");
		localWorkSizeBuffer = BufferUtils.createPointerBuffer(workSizeDimensions);
		for (int i = 0; i < workSizeDimensions; i++) {
			localWorkSizeBuffer.put(localWorkSize[i]);
		}
		localWorkSizeBuffer.flip();
	}

	//---[args]---
	public void setArgBuffer(int index, CLBuffer buffer) {
		clSetKernelArg1p(clKernel, index, buffer.getBufferPointer());

		if (glBuffers != null) {
			if (buffer.isGLBuffer()) {
				for (int i = glBuffers.size(); i <= index; i++) {
					glBuffers.add(null);
				}
				glBuffers.set(index, buffer);
			} else {
				if (glBuffers.size() > index) {
					glBuffers.set(index, null);
				}
			}
		}
	}

	//byte
	public void setArg1b(int index, byte b0) {
		clSetKernelArg1b(clKernel, index, b0);
	}

	public void setArg2b(int index, byte b0, byte b1) {
		clSetKernelArg2b(clKernel, index, b0, b1);
	}

	public void setArg3b(int index, byte b0, byte b1, byte b2) {
		clSetKernelArg4b(clKernel, index, b0, b1, b2, (byte) 0);
	}

	public void setArg4b(int index, byte b0, byte b1, byte b2, byte b3) {
		clSetKernelArg4b(clKernel, index, b0, b1, b2, b3);
	}

	//short
	public void setArg1s(int index, short s0) {
		clSetKernelArg1s(clKernel, index, s0);
	}

	public void setArg2s(int index, short s0, short s1) {
		clSetKernelArg2s(clKernel, index, s0, s1);
	}

	public void setArg3s(int index, short s0, short s1, short s2) {
		clSetKernelArg4s(clKernel, index, s0, s1, s2, (short) 0);
	}

	public void setArg4s(int index, short s0, short s1, short s2, short s3) {
		clSetKernelArg4s(clKernel, index, s0, s1, s2, s3);
	}

	//int
	public void setArg1i(int index, int i0) {
		clSetKernelArg1i(clKernel, index, i0);
	}

	public void setArg2i(int index, int i0, int i1) {
		clSetKernelArg2i(clKernel, index, i0, i1);
	}

	public void setArg3i(int index, int i0, int i1, int i2) {
		clSetKernelArg4i(clKernel, index, i0, i1, i2, 0);
	}

	public void setArg4i(int index, int i0, int i1, int i2, int i3) {
		clSetKernelArg4i(clKernel, index, i0, i1, i2, i3);
	}

	//long
	public void setArg1l(int index, long l0) {
		clSetKernelArg1l(clKernel, index, l0);
	}

	public void setArg2l(int index, long l0, long l1) {
		clSetKernelArg2l(clKernel, index, l0, l1);
	}

	public void setArg3l(int index, long l0, long l1, long l2) {
		clSetKernelArg4l(clKernel, index, l0, l1, l2, 0);
	}

	public void setArg4l(int index, long l0, long l1, long l2, long l3) {
		clSetKernelArg4l(clKernel, index, l0, l1, l2, l3);
	}

	//float
	public void setArg1f(int index, float f0) {
		clSetKernelArg1f(clKernel, index, f0);
	}

	public void setArg2f(int index, float f0, float f1) {
		clSetKernelArg2f(clKernel, index, f0, f1);
	}

	public void setArg3f(int index, float f0, float f1, float f2) {
		clSetKernelArg4f(clKernel, index, f0, f1, f2, 0);
	}

	public void setArg4f(int index, float f0, float f1, float f2, float f3) {
		clSetKernelArg4f(clKernel, index, f0, f1, f2, f3);
	}

	//double
	public void setArg1d(int index, double d0) {
		clSetKernelArg1d(clKernel, index, d0);
	}

	public void setArg2d(int index, double d0, double d1) {
		clSetKernelArg2d(clKernel, index, d0, d1);
	}

	public void setArg3d(int index, double d0, double d1, double d2) {
		clSetKernelArg4d(clKernel, index, d0, d1, d2, 0);
	}

	public void setArg4d(int index, double d0, double d1, double d2, double d3) {
		clSetKernelArg4d(clKernel, index, d0, d1, d2, d3);
	}

	//---[end args]---

	public long enqueueExecution(boolean blockGL, boolean blockCL) {
		long event = 0;

		if (CLContext.IS_PROFILING_ENABLED) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				PointerBuffer eventBuffer = stack.callocPointer(1);
				enqueueExecution(blockGL, blockCL, eventBuffer);
				event = eventBuffer.get(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			enqueueExecution(blockGL, blockCL, null);
		}

		return event;
	}

	private void enqueueExecution(boolean blockGL, boolean blockCL, PointerBuffer eventBuffer) {
		if (blockGL) {
			glFinish();
		}

		//acquire gl objects
		if (glBuffers != null) {
			for (CLBuffer buffer : glBuffers) {
				if (buffer != null) clEnqueueAcquireGLObjects(clProgram.getCLContext().getCLCommandQueue(), buffer.getBufferPointer(), null, null);
			}
		}

		clEnqueueNDRangeKernel(
				clProgram.getCLContext().getCLCommandQueue(),
				clKernel,
				workSizeDimensions,
				null,
				globalWorkSizeBuffer,
				localWorkSizeBuffer,
				null,
				eventBuffer
		                      );

		if (blockCL) clFinish(clProgram.getCLContext().getCLCommandQueue());

		//release gl objects
		if (glBuffers != null) {
			for (CLBuffer buffer : glBuffers) {
				if (buffer != null) CL10GL.clEnqueueReleaseGLObjects(clProgram.getCLContext().getCLCommandQueue(), buffer.getBufferPointer(), null, null);
			}
		}
		if (blockCL) clFinish(clProgram.getCLContext().getCLCommandQueue());
	}

	public long getProfilingTime(long event) {
		if (!CLContext.IS_PROFILING_ENABLED) return 0;
		clWaitForEvents(event);
		long[] timeStart = { 0 };
		long[] timeEnd = { 0 };

		clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_START, timeStart, null);
		clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_END, timeEnd, null);

		return timeEnd[0] - timeStart[0];
	}

}
