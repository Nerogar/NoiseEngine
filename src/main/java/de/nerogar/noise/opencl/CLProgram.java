package de.nerogar.noise.opencl;

import de.nerogar.noise.Noise;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opencl.CL10.*;

public class CLProgram {

	public static final  boolean IS_VERBOSE_COMPILER_ENABLED;
	private static final String  COMPILER_OPTIONS;

	private static final String    ERROR_LOCATION = "CLProgram";
	private              IntBuffer errorCode;

	private CLContext clContext;

	private long clProgram;

	public CLProgram(CLContext clContext, String source) {
		this.clContext = clContext;
		errorCode = BufferUtils.createIntBuffer(1);

		clProgram = clCreateProgramWithSource(clContext.getCLContext(), source, errorCode);

		CLContext.checkCLError(errorCode, ERROR_LOCATION);

		build();
	}

	private void build() {
		int buildErrorCode = clBuildProgram(clProgram, clContext.getCLDevice().getClDevicePointer(), COMPILER_OPTIONS, null, 0L);
		if (buildErrorCode != CL_SUCCESS || IS_VERBOSE_COMPILER_ENABLED) {
			logBuildError(clProgram, clContext.getCLDevice().getClDevicePointer());
		}
		CLContext.checkCLError(buildErrorCode, ERROR_LOCATION);
	}

	private static void logBuildError(long program, long device) {
		PointerBuffer lengthBuffer = BufferUtils.createPointerBuffer(1);
		clGetProgramBuildInfo(program, device, CL10.CL_PROGRAM_BUILD_LOG, (ByteBuffer) null, lengthBuffer);
		int length = (int) lengthBuffer.get();
		ByteBuffer errorbuffer = BufferUtils.createByteBuffer(length);
		clGetProgramBuildInfo(program, device, CL10.CL_PROGRAM_BUILD_LOG, errorbuffer, null);

		StringBuilder sb = new StringBuilder(length + 1);
		for (int i = 0; i < errorbuffer.capacity(); i++) {
			sb.append((char) errorbuffer.get());
		}
		sb.append("\n");

		Noise.getLogger().log(Logger.ERROR, sb.toString());
	}

	public long getCLProgram() {
		return clProgram;
	}

	public CLContext getCLContext() {
		return clContext;
	}

	static {
		String options = "";

		boolean verboseCompilerEnabled = false;

		if (Noise.getSettings().contains("openCL")) {
			NDSNodeObject openClProperties = Noise.getSettings().getObject("openCL");
			if (openClProperties.getBoolean("enableVerboseCompiler")) {
				options += " -cl-nv-verbose";
				verboseCompilerEnabled = true;
			}
		}

		IS_VERBOSE_COMPILER_ENABLED = verboseCompilerEnabled;

		COMPILER_OPTIONS = options;
	}

}
