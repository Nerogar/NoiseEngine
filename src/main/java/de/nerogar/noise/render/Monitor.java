package de.nerogar.noise.render;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Monitor {

	private static List<Monitor> monitors;

	private final long   pointer;
	private final String name;

	private final int width;
	private final int height;

	private Monitor(long pointer) {
		this.pointer = pointer;
		this.name = glfwGetMonitorName(pointer);

		GLFWVidMode videoMode = glfwGetVideoMode(pointer);
		width = videoMode.width();
		height = videoMode.height();
	}

	public long getPointer() {
		return pointer;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "Monitor: [@" + Long.toHexString(pointer) + ": " + name + "]";
	}

	public static Monitor[] getMonitors() {
		return monitors.toArray(new Monitor[monitors.size()]);
	}

	static {
		monitors = new ArrayList<>();

		PointerBuffer monitorBuffer = glfwGetMonitors();
		while (monitorBuffer.hasRemaining()) {
			long monitorPointer = monitorBuffer.get();
			monitors.add(new Monitor(monitorPointer));
		}

		glfwSetMonitorCallback((long monitor, int event) -> {
			if (event == GLFW_CONNECTED) {
				monitors.add(new Monitor(monitor));
			} else if (event == GLFW_DISCONNECTED) {
				monitors.removeIf(a -> a.pointer == monitor);
			}
		});
	}

}
