package de.nerogar.noise.render;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWMonitorCallback;

public class Monitor {

	private static List<Monitor> monitors;

	private long pointer;
	private String name;

	private Monitor(long pointer, String name) {
		this.pointer = pointer;
		this.name = name;
	}

	public long getPointer() {
		return pointer;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Monitor: [@" + Long.toHexString(pointer) + ": " + name + "]";
	}

	public static Monitor[] getMonitors() {
		return monitors.toArray(new Monitor[monitors.size()]);
	}

	static {
		monitors = new ArrayList<Monitor>();

		PointerBuffer monitorBuffer = glfwGetMonitors();
		while (monitorBuffer.hasRemaining()) {
			long monitorPointer = monitorBuffer.get();
			monitors.add(new Monitor(monitorPointer, glfwGetMonitorName(monitorPointer)));
		}

		glfwSetMonitorCallback(new GLFWMonitorCallback() {
			@Override
			public void invoke(long monitor, int event) {
				if (event == GLFW_CONNECTED) {
					monitors.add(new Monitor(monitor, glfwGetMonitorName(monitor)));
				} else if (event == GLFW_DISCONNECTED) {
					monitors.removeIf(a -> a.pointer == monitor);
				}
			}
		});
	}

}
