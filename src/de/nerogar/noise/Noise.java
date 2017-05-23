package de.nerogar.noise;

import de.nerogar.noise.debug.DebugWindow;
import de.nerogar.noise.debug.ResourceProfiler;
import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSReader;
import de.nerogar.noise.sound.ALContext;
import de.nerogar.noise.util.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;

public class Noise {

	/**
	 * the location of default engine ressources.
	 */
	public static String RESOURCE_DIR;

	private static final String SETTINGS_PROPERTY = "noise.settings";
	private static final String SETTINGS_FILENAME = "noiseEngine/settings.json";
	private static NDSNodeObject settings;

	private static boolean initialized = false;

	private static ALContext alContext;

	private static DebugWindow      debugWindow;
	private static ResourceProfiler resourceProfiler;

	//hold our own reference, GLFW doesn't like to do that itself
	private static GLFWErrorCallback errorCallbackFun;

	/**
	 * Initialize the Noise library. The default settings file is used.
	 */
	public static void init() {
		init(SETTINGS_FILENAME);
	}

	/**
	 * Initialize the Noise library with a custom settings file.
	 *
	 * @param settingsFilename the filename of the settings file
	 */
	public static void init(String settingsFilename) {
		if (!initialized) {
			loadSettings(settingsFilename);

			glfwInit();

			Logger.log(Logger.INFO, "GLFW initialized, version: " + GLFW.GLFW_VERSION_MAJOR + "." + GLFW.GLFW_VERSION_MINOR + "." + GLFW.GLFW_VERSION_REVISION);

			errorCallbackFun = GLFWErrorCallback.createPrint(Logger.getErrorStream());
			glfwSetErrorCallback(errorCallbackFun);

			resourceProfiler = new ResourceProfiler();
			debugWindow = new DebugWindow(resourceProfiler);

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

	private static void loadSettings(String settingsFilename) {

		NDSFile settingsFile = null;

		if (System.getProperties().containsKey(SETTINGS_PROPERTY)) {
			settingsFilename = System.getProperty(SETTINGS_PROPERTY);
			Logger.log(Logger.INFO, "settings file was overwritten by parameter (file: \"" + settingsFilename + "\")");
		}

		try {
			settingsFile = NDSReader.readJsonFile(settingsFilename);
		} catch (FileNotFoundException e) {
			Logger.log(Logger.WARNING, "could not find settings file, loading default");
			try {
				settingsFile = NDSReader.readJsonFile(SETTINGS_FILENAME);
			} catch (FileNotFoundException e1) {
				Logger.log(Logger.ERROR, "could not load default settings file, aborting");
				System.exit(1);
			}

		}

		settings = settingsFile.getData();

		RESOURCE_DIR = settings.getObject("noise").getStringUTF8("resourceDir");

	}

	public static DebugWindow getDebugWindow() {
		return debugWindow;
	}

	public static ResourceProfiler getResourceProfiler() {
		return resourceProfiler;
	}

	public static NDSNodeObject getSettings() {
		return settings;
	}

	public static void cleanup() {
		glfwTerminate();
		alContext.destroy();
	}

}
