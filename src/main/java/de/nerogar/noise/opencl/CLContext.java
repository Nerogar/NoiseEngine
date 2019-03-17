package de.nerogar.noise.opencl;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.GLContext;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFWNativeWGL.glfwGetWGLContext;
import static org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Display;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.KHRGLSharing.*;
import static org.lwjgl.opengl.CGL.CGLGetShareGroup;
import static org.lwjgl.opengl.WGL.wglGetCurrentDC;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class CLContext {

	public static final  boolean IS_PROFILING_ENABLED;
	private static final long    CONTEXT_PROPERTIES;

	private static final String    ERROR_LOCATION = "CLContext";
	private              IntBuffer errorCode;

	private boolean   shareGL;
	private GLContext glContext;

	private long clContextPointer;

	private CLDevice clDevice;
	private long     clCommandQueue;

	public CLContext(GLContext glContext) {
		this.glContext = glContext;
		shareGL = true;

		errorCode = BufferUtils.createIntBuffer(1);

		// get a list with devices for gl sharing
		List<CLDevice> devices = null;
		CLPlatform platform = null;

		for (CLPlatform platformIter : CLPlatform.getPlatforms()) {
			try {
				devices = platformIter.getDevices(CL_DEVICE_TYPE_GPU)
						.stream()
						.filter(device -> device.getCapabilities().cl_khr_gl_sharing)
						.collect(Collectors.toList());
				platform = platformIter;
			} catch (CLException ignored) {
			}
		}
		boolean isSharingPossible = devices != null && !devices.isEmpty();
		if (!isSharingPossible) throw new CLException("CL-GL context sharing is not supported.");

		clDevice = devices.get(0);

		// fill a buffer with the first filtered device
		PointerBuffer deviceBuffer = BufferUtils.createPointerBuffer(1);
		deviceBuffer.put(0, clDevice.getClDevicePointer());

		// create a buffer with context creation properties
		PointerBuffer contextProperties = BufferUtils.createPointerBuffer(7);
		contextProperties.put(CL_CONTEXT_PLATFORM).put(platform.getClPlatformPointer());
		contextProperties.put(CL_GL_CONTEXT_KHR).put(glfwGetWGLContext(glContext.getGlContextPointer()));

		switch (Platform.get()) {
			case WINDOWS:
				contextProperties.put(CL_WGL_HDC_KHR).put(wglGetCurrentDC());
				break;
			case LINUX:
				contextProperties.put(CL_GLX_DISPLAY_KHR).put(glfwGetX11Display());
				break;
			case MACOSX:
				contextProperties.put(CL_CGL_SHAREGROUP_KHR).put(CGLGetShareGroup(glContext.getGlContextPointer()));
				break;
		}

		contextProperties.put(0); // add a NULL terminator

		contextProperties.flip();

		checkCLError(errorCode, ERROR_LOCATION);

		// create the context
		clContextPointer = clCreateContext(contextProperties, deviceBuffer, (errinfo, private_info, cb, user_data) -> {
			Noise.getLogger().log(Logger.ERROR, "openCL error: " + memUTF8(errinfo));
		}, NULL, errorCode);

		checkCLError(errorCode, ERROR_LOCATION);

		clCommandQueue = clCreateCommandQueue(clContextPointer, clDevice.getClDevicePointer(), CONTEXT_PROPERTIES, errorCode);
		checkCLError(errorCode, ERROR_LOCATION);
	}

	public CLContext() {
		shareGL = false;

		errorCode = BufferUtils.createIntBuffer(1);

		//get a list with devices
		CLPlatform clPlatform = CLPlatform.getPlatforms().get(0);
		List<CLDevice> devices = clPlatform.getDevices(CL_DEVICE_TYPE_GPU);
		clDevice = devices.get(0);

		//fill a buffer with the first filtered device
		PointerBuffer deviceBuffer = BufferUtils.createPointerBuffer(1);
		deviceBuffer.put(0, clDevice.getClDevicePointer());

		//create a buffer with context creation properties
		PointerBuffer contextProperties = BufferUtils.createPointerBuffer(3);
		contextProperties.put(0, CL_CONTEXT_PLATFORM).put(1, clPlatform.getClPlatformPointer());
		contextProperties.put(2, 0); //add a NULL terminator

		//create the context
		clContextPointer = clCreateContext(contextProperties, deviceBuffer, (errinfo, private_info, cb, user_data) -> {
			Noise.getLogger().log(Logger.ERROR, "openCL error: " + memUTF8(errinfo));
		}, NULL, errorCode);

		checkCLError(errorCode, ERROR_LOCATION);

		clCommandQueue = clCreateCommandQueue(clContextPointer, clDevice.getClDevicePointer(), CONTEXT_PROPERTIES, errorCode);
		checkCLError(errorCode, ERROR_LOCATION);
	}

	public boolean isContextGLShared() {
		return shareGL;
	}

	public long getCLContext() {
		return clContextPointer;
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
			Noise.getLogger().log(Logger.ERROR, "OpenCL error: " + errorCode + " in " + errorLocation);
			throw new CLException("openCL error code " + errorCode);
		}
	}

	static {
		long properties = 0;

		boolean profilingEnabled = false;

		if (Noise.getSettings().contains("openCL")) {
			NDSNodeObject openClProperties = Noise.getSettings().getObject("openCL");
			if (openClProperties.getBoolean("enableProfiling")) {
				properties |= CL_QUEUE_PROFILING_ENABLE;
				profilingEnabled = true;
			}
		}

		IS_PROFILING_ENABLED = profilingEnabled;

		CONTEXT_PROPERTIES = properties;
	}

}
