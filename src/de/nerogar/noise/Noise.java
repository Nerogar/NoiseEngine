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
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;

public class Noise {

	/**
	 * the location of default engine ressources.
	 */
	public static String RESOURCE_DIR;

	private static Logger logger;

	private static final String SETTINGS_PROPERTY = "noise.settings";
	private static final String SETTINGS_FILENAME = "noiseEngine/settings.json";
	private static NDSNodeObject settings;

	private static boolean initialized = false;

	private static ALContext alContext;

	private static DebugWindow      debugWindow;
	private static ResourceProfiler resourceProfiler;

	// hold our own reference, GLFW doesn't like to do that itself
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

			getLogger().log(Logger.INFO, "GLFW initialized, version: " + GLFW.GLFW_VERSION_MAJOR + "." + GLFW.GLFW_VERSION_MINOR + "." + GLFW.GLFW_VERSION_REVISION);

			errorCallbackFun = GLFWErrorCallback.createPrint(Noise.getLogger().getErrorStream());
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
			getLogger().log(Logger.INFO, "Noise initialized, LWJGL version: " + Version.getVersion());
		}

		initialized = true;
	}

	private static void loadSettings(String settingsFilename) {

		NDSFile settingsFile = null;

		if (System.getProperties().containsKey(SETTINGS_PROPERTY)) {
			settingsFilename = System.getProperty(SETTINGS_PROPERTY);
			Noise.getLogger().log(Logger.DEBUG, "settings file was overwritten by parameter (file: \"" + settingsFilename + "\")");
		}

		try {
			settingsFile = NDSReader.readJsonFile(settingsFilename);
		} catch (FileNotFoundException e) {
			Noise.getLogger().log(Logger.WARNING, "could not find settings file, loading default");
			try {
				settingsFile = NDSReader.readJsonFile(SETTINGS_FILENAME);
			} catch (FileNotFoundException e1) {
				Noise.getLogger().log(Logger.ERROR, "could not load default settings file, aborting");
				System.exit(1);
			}

		}

		settings = settingsFile.getData();

		loadLoggerSettings(settings.getObject("logger"));

		RESOURCE_DIR = settings.getObject("noise").getStringUTF8("resourceDir");

	}

	private static void loadLoggerSettings(NDSNodeObject loggerSettings) {

		getLogger().setPrintTimestamp(loggerSettings.getBoolean("printTimestamp"));
		getLogger().setPrintName(loggerSettings.getBoolean("printName"));

		NDSNodeObject[] defaultLoggerConfigs = loggerSettings.getObjectArray("defaultLoggerOutput");

		for (NDSNodeObject defaultLoggerConfig : defaultLoggerConfigs) {
			NDSNodeObject parameters = defaultLoggerConfig.getObject("parameters");
			switch (defaultLoggerConfig.getStringUTF8("type")) {
				case "stdOut":
					getLogger().addStream(
							Logger.getLogLevel(defaultLoggerConfig.getStringUTF8("minLevel")),
							Logger.getLogLevel(defaultLoggerConfig.getStringUTF8("maxLevel")),
							System.out
					                     );
					break;
				case "stdErr":
					getLogger().addStream(
							Logger.getLogLevel(defaultLoggerConfig.getStringUTF8("minLevel")),
							Logger.getLogLevel(defaultLoggerConfig.getStringUTF8("maxLevel")),
							System.err
					                     );
					break;
				case "file":

					String filename = parameters.getStringUTF8("filename");
					boolean append = parameters.getBoolean("append");

					try {
						PrintStream out = new PrintStream(new FileOutputStream(filename, append));
						getLogger().addStream(
								Logger.getLogLevel(defaultLoggerConfig.getStringUTF8("minLevel")),
								Logger.getLogLevel(defaultLoggerConfig.getStringUTF8("maxLevel")),
								out
						                     );
					} catch (FileNotFoundException e) {
						e.printStackTrace(getLogger().getWarningStream());
					}
					break;
			}
		}

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

	public static Logger getLogger() {
		if (logger == null) {
			logger = new Logger("Noise");
		}
		return logger;
	}

	public static void cleanup() {
		glfwTerminate();
		alContext.destroy();
	}

}
