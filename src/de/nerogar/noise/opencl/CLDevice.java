package de.nerogar.noise.opencl;

import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;

public class CLDevice {

	private long clDevicePointer;

	private CLCapabilities capabilities;

	protected CLDevice(long clDevicePointer, CLCapabilities clPlatformCapabilities) {
		this.clDevicePointer = clDevicePointer;
		this.capabilities = CL.createDeviceCapabilities(clDevicePointer, clPlatformCapabilities);
	}

	public long getClDevicePointer() {
		return clDevicePointer;
	}

	public CLCapabilities getCapabilities() {
		return capabilities;
	}
}
