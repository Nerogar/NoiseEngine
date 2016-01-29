package de.nerogar.noise.opencl;

import static org.lwjgl.glfw.GLFWLinux.glfwGetX11Display;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.KHRGLSharing.*;
import static org.lwjgl.opengl.CGL.CGLGetShareGroup;
import static org.lwjgl.opengl.WGL.wglGetCurrentDC;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memDecodeUTF8;

import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.*;
import org.lwjgl.opencl.*;
import org.lwjgl.opengl.GLContext;

import de.nerogar.noise.util.Logger;

public class CLContext {

	private static final String ERROR_LOCATION = "CLContext";
	private IntBuffer errorCode;

	private boolean shareGL;
	private GLContext glContext;

	private long clContext;

	private CLDevice clDevice;
	private long clCommandQueue;

	public CLContext(GLContext glContext) {
		this.glContext = glContext;
		shareGL = true;

		errorCode = BufferUtils.createIntBuffer(1);

		//get a list with devices for gl sharing
		CLPlatform platform = CLPlatform.getPlatforms().get(0);
		List<CLDevice> devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU, (device) -> {
			return device.getCapabilities().cl_khr_gl_sharing;
		});
		boolean isSharingPossible = !devices.isEmpty();
		if (!isSharingPossible) throw new RuntimeException("CL-GL context sharing is not supported.");

		clDevice = devices.get(0);

		//fill a buffer with the first filtered device
		PointerBuffer deviceBuffer = BufferUtils.createPointerBuffer(1);
		deviceBuffer.put(0, clDevice.getPointer());

		//create a buffer with context creation properties
		PointerBuffer contextProperties = BufferUtils.createPointerBuffer(7);
		contextProperties.put(0, CL_CONTEXT_PLATFORM).put(1, platform.getPointer());
		contextProperties.put(2, CL_GL_CONTEXT_KHR).put(3, glContext.getPointer());

		switch (LWJGLUtil.getPlatform()) {
		case WINDOWS:
			contextProperties.put(4, CL_WGL_HDC_KHR).put(5, wglGetCurrentDC());
			break;
		case LINUX:
			contextProperties.put(4, CL_GLX_DISPLAY_KHR).put(5, glfwGetX11Display());
			break;
		case MACOSX:
			contextProperties.put(4, CL_CGL_SHAREGROUP_KHR).put(5, CGLGetShareGroup(glContext.getPointer()));
			break;
		}

		contextProperties.put(6, 0); //add a NULL terminator

		//create the context
		clContext = clCreateContext(contextProperties, deviceBuffer, new CLCreateContextCallback() {
			@Override
			public void invoke(long errinfo, long private_info, long cb, long user_data) {
				Logger.log(Logger.ERROR, "openCL error: " + memDecodeUTF8(errinfo));
			}
		}, NULL, errorCode);

		checkCLError(errorCode, ERROR_LOCATION);

		clCommandQueue = clCreateCommandQueue(clContext, clDevice.getPointer(), 0L, errorCode);
		checkCLError(errorCode, ERROR_LOCATION);
	}

	public CLContext() {
		shareGL = false;

		errorCode = BufferUtils.createIntBuffer(1);

		//get a list with devices for gl sharing
		CLPlatform platform = CLPlatform.getPlatforms().get(0);
		List<CLDevice> devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
		clDevice = devices.get(0);

		//fill a buffer with the first filtered device
		PointerBuffer deviceBuffer = BufferUtils.createPointerBuffer(1);
		deviceBuffer.put(0, clDevice.getPointer());

		//create a buffer with context creation properties
		PointerBuffer contextProperties = BufferUtils.createPointerBuffer(3);
		contextProperties.put(0, CL_CONTEXT_PLATFORM).put(1, platform.getPointer());
		contextProperties.put(2, 0); //add a NULL terminator

		//create the context
		clContext = clCreateContext(contextProperties, deviceBuffer, new CLCreateContextCallback() {
			@Override
			public void invoke(long errinfo, long private_info, long cb, long user_data) {
				Logger.log(Logger.ERROR, "openCL error: " + memDecodeUTF8(errinfo));
			}
		}, NULL, errorCode);

		checkCLError(errorCode, ERROR_LOCATION);

		clCommandQueue = clCreateCommandQueue(clContext, clDevice.getPointer(), 0L, errorCode);
		checkCLError(errorCode, ERROR_LOCATION);
	}

	public boolean isContextGLShared() {
		return shareGL;
	}

	public long getCLContext() {
		return clContext;
	}

	public GLContext getGLContext() {
		return glContext;
	}

	public CLDevice getCLDevice() {
		return clDevice;
	}

	public long getCLCommandQueue() {
		return clCommandQueue;
	}

	public static void checkCLError(IntBuffer errorCode, String errorLocation) {
		checkCLError(errorCode.get(errorCode.position()), errorLocation);
	}

	public static void checkCLError(int errorCode, String errorLocation) {
		if (errorCode != CL_SUCCESS) {
			Logger.log(Logger.ERROR, "OpenCL error: " + CLUtil.getErrcodeName(errorCode) + " in " + errorLocation);
			throw new OpenCLException(CLUtil.getErrcodeName(errorCode));
		}
	}

}
