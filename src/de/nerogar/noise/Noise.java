package de.nerogar.noise;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;

public class Noise {
	private static boolean initialized = false;

	public static void init() {
		if (!initialized) {
			glfwInit();

			glfwSetErrorCallback(errorCallbackPrint(System.err));
		}

		initialized = true;
	}

	public static void cleanup() {
		glfwTerminate();
	}
}
