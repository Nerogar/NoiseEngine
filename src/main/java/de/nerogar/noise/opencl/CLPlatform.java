package de.nerogar.noise.opencl;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLCapabilities;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opencl.CL.createPlatformCapabilities;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clGetPlatformIDs;

public class CLPlatform {

	private static final String ERROR_LOCATION = "CLPlatform";

	private long clPlatformPointer;

	private CLCapabilities capabilities;

	private CLPlatform(long clPlatformPointer) {
		this.clPlatformPointer = clPlatformPointer;

		capabilities = createPlatformCapabilities(clPlatformPointer);
	}

	public long getClPlatformPointer() {
		return clPlatformPointer;
	}

	public CLCapabilities getCapabilities() {
		return capabilities;
	}

	public List<CLDevice> getDevices(int type) {
		int[] length = new int[1];

		int error = clGetDeviceIDs(clPlatformPointer, type, null, length);
		CLContext.checkCLError(error, ERROR_LOCATION);

		PointerBuffer buffer = BufferUtils.createPointerBuffer(length[0]);

		error = clGetDeviceIDs(clPlatformPointer, type, buffer, length);
		CLContext.checkCLError(error, ERROR_LOCATION);

		List<CLDevice> devices = new ArrayList<>();
		for (int i = 0; i < length[0]; i++) {
			devices.add(new CLDevice(buffer.get(i), capabilities));
		}

		return devices;
	}

	public static List<CLPlatform> getPlatforms() {

		IntBuffer length = BufferUtils.createIntBuffer(1);
		//int[] length = new int[1];

		int error = clGetPlatformIDs(null, length);
		CLContext.checkCLError(error, ERROR_LOCATION);

		PointerBuffer buffer = BufferUtils.createPointerBuffer(length.get(0));

		error = clGetPlatformIDs(buffer, length);
		CLContext.checkCLError(error, ERROR_LOCATION);

		List<CLPlatform> platforms = new ArrayList<>();
		for (int i = 0; i < length.get(0); i++) {
			platforms.add(new CLPlatform(buffer.get(i)));
		}

		return platforms;
	}

}
