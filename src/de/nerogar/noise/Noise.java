package de.nerogar.noise;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALContext;

import de.nerogar.noise.debug.DebugWindow;
import de.nerogar.noise.debug.RessourceProfiler;

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

	private static DebugWindow debugWindow;
	private static RessourceProfiler ressourceProfiler;

	public static void init() {
		if (!initialized) {
			glfwInit();
			glfwSetErrorCallback(errorCallbackPrint(System.err));

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

			alContext = ALContext.create();
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
		AL.destroy(alContext);
	}
}
