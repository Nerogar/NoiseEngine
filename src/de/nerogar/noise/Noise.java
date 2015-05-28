package de.nerogar.noise;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;

public class Noise {
	private static boolean initialized = false;

	public static void init() {
		if (!initialized) {
			glfwInit();
			glfwSetErrorCallback(errorCallbackPrint(System.err));

			//sleeping thread for timer precision on windows
			Thread sleepThread = new Thread("sleeping thread") {
				@Override
				public void run() {
					try {
						while (true) {
							Thread.sleep(Long.MAX_VALUE);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

			sleepThread.setDaemon(true);
			sleepThread.start();
		}

		initialized = true;
	}

	public static void cleanup() {
		glfwTerminate();
	}
}
