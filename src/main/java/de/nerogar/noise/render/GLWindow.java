package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.input.GlfwJoystickInputHandler;
import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.render.vr.OvrContext;
import de.nerogar.noise.util.NoiseResource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLWindow extends NoiseResource implements IRenderTarget {

	private static List<GLWindow> windows;
	private static GLWindow       currentWindow;
	private static long           currentGlContext;

	private long windowPointer;

	private String  title;
	private int     windowWidth;
	private int     windowHeight;
	private int     swapInterval;
	private boolean resizable;

	private GLContext  glContext;
	private OvrContext ovrContext;

	private GLWindowSizeChangeListener sizeChangeListener;
	private GLWindowRefreshListener    refreshListener;

	private InputHandler             inputHandler;
	private GlfwJoystickInputHandler glfwJoystickInputHandler;

	//holds references to callbacks, otherwise the gc will delete them

	private GLFWFramebufferSizeCallbackI frameBufferCallback;
	private GLFWWindowRefreshCallbackI   windowRefreshCallback;

	private boolean isClosed;

	/**
	 * Creates a new window for OpenGL. A new GLContext will be created.
	 * To share objects like textures or vertex buffers between windows, use the <code>parentWindow</code> parameter
	 *
	 * @param title        initial window Title
	 * @param width        initial window width
	 * @param height       initial window height
	 * @param resizable    initial resizable status
	 * @param swapInterval initial swap interval (0 = unbound)
	 */
	public GLWindow(String title, int width, int height, boolean resizable, int swapInterval) {
		this(title, width, height, resizable, swapInterval, null, null);
	}

	/**
	 * Creates a new window for OpenGL. A new GLContext will be created.
	 * To share objects like textures or vertex buffers between windows, use the <code>parentWindow</code> parameter
	 *
	 * @param title        initial window Title
	 * @param width        initial window width
	 * @param height       initial window height
	 * @param resizable    initial resizable status
	 * @param swapInterval initial swap interval (0 = unbound)
	 * @param monitor      the monitor for fullscreen windows, null otherwise
	 * @param parentWindow the window to share openGL objects with, null otherwise
	 */
	public GLWindow(String title, int width, int height, boolean resizable, int swapInterval, Monitor monitor, GLWindow parentWindow) {
		super(title);

		windows.add(this);
		this.title = title;
		this.resizable = resizable;
		this.windowWidth = width;
		this.windowHeight = height;

		inputHandler = new InputHandler();

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, GL_FALSE);

		windowPointer = glfwCreateWindow(width, height, title, monitor == null ? NULL : monitor.getPointer(), parentWindow == null ? NULL : parentWindow.windowPointer);
		glfwSetInputMode(windowPointer, GLFW_STICKY_KEYS, GL_TRUE);
		inputHandler.init(this, windowPointer);
		glfwJoystickInputHandler = new GlfwJoystickInputHandler(inputHandler);

		GLWindow.makeContextCurrent(windowPointer);
		glContext = new GLContext(windowPointer);//GLContext.createFromCurrent();

		setSwapInterval(Math.max(0, swapInterval));

		frameBufferCallback = (long window, int newWidth, int newHeight) -> {
			long currentContext = GLWindow.getCurrentContext();
			GLWindow.makeContextCurrent(windowPointer);
			glViewport(0, 0, newWidth, newHeight);
			GLWindow.makeContextCurrent(currentContext);

			windowWidth = newWidth;
			windowHeight = newHeight;

			if (sizeChangeListener != null) sizeChangeListener.onChange(newWidth, newHeight);
		};

		windowRefreshCallback = (long window) -> {
			if (refreshListener != null) {
				refreshListener.onRefresh(window);
			}
		};

		glfwSetFramebufferSizeCallback(windowPointer, frameBufferCallback);
		glfwSetWindowRefreshCallback(windowPointer, windowRefreshCallback);

		if (GLFW.glfwRawMouseMotionSupported()) {
			glfwSetInputMode(windowPointer, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
		}

		bind();
	}

	public void setOvrContext(OvrContext ovrContext) {
		this.ovrContext = ovrContext;
	}

	@Override
	public void setResolution(int width, int height) {
		this.windowWidth = width;
		this.windowHeight = height;
		glfwSetWindowSize(windowPointer, width, height);
	}

	@Override
	public int getWidth() {
		return windowWidth;
	}

	@Override
	public int getHeight() {
		return windowHeight;
	}

	/**
	 * sets the fullscreen mode. null means no fullscreen.
	 *
	 * @param monitor the target monitor or null
	 */
	public void setFullscreen(Monitor monitor) {
		glfwSetWindowMonitor(
				windowPointer,
				monitor == null ? NULL : monitor.getPointer(),
				100, 100,
				getWidth(), getHeight(),
				monitor == null ? GLFW_DONT_CARE : monitor.getRefreshRate()
		                    );
	}

	public void setSwapInterval(int swapInterval) {
		this.swapInterval = swapInterval;
		glfwSwapInterval(swapInterval);
	}

	public int getSwapInterval() {
		return swapInterval;
	}

	public void setShouldClose(boolean shouldClose) {
		glfwSetWindowShouldClose(windowPointer, shouldClose);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowPointer);
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setTitle(String title) {
		this.title = title;
		glfwSetWindowTitle(windowPointer, title);
	}

	public String getTitle() {
		return title;
	}

	public boolean isResizable() {
		return resizable;
	}

	public OvrContext getOvrContext() {
		return ovrContext;
	}

	public GLContext getGLContext() {
		return glContext;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public void setSizeChangeListener(GLWindowSizeChangeListener sizeChangeListener) {
		this.sizeChangeListener = sizeChangeListener;
	}

	public void setWindowRefreshListener(GLWindowRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public void minimize() {
		glfwIconifyWindow(windowPointer);
	}

	public void show() {
		glfwRestoreWindow(windowPointer);
		glfwShowWindow(windowPointer);
	}

	public void makeContextCurrent() {
		makeContextCurrent(windowPointer);
	}

	@Override
	public void bind() {
		GLWindow.makeContextCurrent(windowPointer);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, windowWidth, windowHeight);

		currentWindow = this;
	}

	public boolean cleanup() {
		if (!super.cleanup()) return false;

		glfwDestroyWindow(windowPointer);
		windows.remove(this);

		isClosed = true;

		return true;
	}

	public static GLWindow getCurrentWindow() {
		return currentWindow;
	}

	public static long getCurrentContext() {
		assert currentGlContext == glfwGetCurrentContext();

		return currentGlContext;
	}

	public static void makeContextCurrent(long glContext) {
		glfwMakeContextCurrent(glContext);
		currentGlContext = glContext;
	}

	public static GLWindow getWindow(long glContext) {
		for (GLWindow win : windows) {
			if (win.getGLContext().getGlContextPointer() == glContext) return win;
		}

		return null;
	}

	/**
	 * Updates all events and swaps buffers on all windows.
	 */
	public static void updateAll() {
		for (GLWindow window : windows) {
			window.inputHandler.update();
			window.glfwJoystickInputHandler.update();

			if (window.ovrContext != null) {
				window.ovrContext.update();
			}

			glfwSwapBuffers(window.windowPointer);
		}

		glfwPollEvents();
		Noise.getResourceProfiler().reset();
		Noise.getDebugWindow().update();
	}

	static {
		windows = new ArrayList<>();
	}

}
