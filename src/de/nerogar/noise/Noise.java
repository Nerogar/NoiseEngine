package de.nerogar.noise;

import de.nerogar.noise.debug.DebugWindow;
import de.nerogar.noise.debug.RessourceProfiler;
import de.nerogar.noise.sound.ALContext;
import de.nerogar.noise.util.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Noise {

	/**
	 * the location of default engine ressources.<br>
	 * set the system property "noise.ressourceDir" to change the location
	 */
	public static final String RESSOURCE_DIR = System.getProperty("noise.ressourceDir", "noiseEngine/");

	/**
	 * true if the engine is in debug state.<br>
	 * set the system property "noise.debug" to true/false to change the state
	 */
	public static final boolean DEBUG = Boolean.getBoolean("noise.debug");

	private static boolean initialized = false;

	private static ALContext alContext;

	private static DebugWindow       debugWindow;
	private static RessourceProfiler ressourceProfiler;

	//hold our own reference, GLFW doesn't like to do that itself
	private static GLFWErrorCallback errorCallbackFun;

	public static void init() {
		if (!initialized) {
			glfwInit();

			Logger.log(Logger.INFO, "GLFW initialized, version: " + GLFW.GLFW_VERSION_MAJOR + "." + GLFW.GLFW_VERSION_MINOR + "." + GLFW.GLFW_VERSION_REVISION);

			errorCallbackFun = GLFWErrorCallback.createPrint(Logger.getErrorStream());
			glfwSetErrorCallback(errorCallbackFun);

			ressourceProfiler = new RessourceProfiler();
			debugWindow = new DebugWindow(ressourceProfiler);

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

			alContext = new ALContext();
			Logger.log(Logger.INFO, "Noise initialized, LWJGL version: " + Version.getVersion());
		}

		initialized = true;
	}

	public static DebugWindow getDebugWindow() {
		return debugWindow;
	}

	public static RessourceProfiler getRessourceProfiler() {
		return ressourceProfiler;
	}

	public static void cleanup() {
		glfwTerminate();
		alContext.destroy();
	}
}
