package de.nerogar.noise.render;

import de.nerogar.noise.Noise;
import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.util.Logger;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLWindow implements IRenderTarget {

	private static List<GLWindow> windows;
	private static GLWindow       currentWindow;
	private static long           currentGlContext;

	private long windowPointer;

	private String  title;
	private int     windowWidth;
	private int     windowHeight;
	private int     swapInterval;
	private boolean resizable;

	private GLContext glContext;

	private GLWindowSizeChangeListener sizeChangeListener;

	private InputHandler inputHandler;

	//holds references to callbacks, otherwise the gc will delete them

	private GLFWFramebufferSizeCallback frameBufferCallback;

	private boolean deleted;

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
	 * @param parentWindow the window to share opengl objects with, null otherwise
	 */
	public GLWindow(String title, int width, int height, boolean resizable, int swapInterval, Monitor monitor, GLWindow parentWindow) {
		windows.add(this);
		this.title = title;
		this.resizable = resizable;
		this.windowWidth = width;
		this.windowHeight = height;

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, GL_FALSE);

		windowPointer = glfwCreateWindow(width, height, title, monitor == null ? NULL : monitor.getPointer(), parentWindow == null ? NULL : parentWindow.windowPointer);
		glfwSetInputMode(windowPointer, GLFW_STICKY_KEYS, GL_TRUE);
		inputHandler = new InputHandler(this, windowPointer);

		GLWindow.makeContextCurrent(windowPointer);
		glContext = new GLContext(windowPointer);//GLContext.createFromCurrent();

		setSwapInterval(swapInterval > 0 ? swapInterval : 0);

		frameBufferCallback = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int newWidth, int newHeight) {
				long currentContext = GLWindow.getCurrentContext();
				GLWindow.makeContextCurrent(windowPointer);
				glViewport(0, 0, newWidth, newHeight);
				GLWindow.makeContextCurrent(currentContext);

				windowWidth = newWidth;
				windowHeight = newHeight;

				if (sizeChangeListener != null) sizeChangeListener.onChange(newWidth, newHeight);
			}
		};

		glfwSetFramebufferSizeCallback(windowPointer, frameBufferCallback);

		bind();
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
		glfwSetWindowMonitor(windowPointer, monitor == null ? NULL : monitor.getPointer(), 100, 100, getWidth(), getHeight(), getSwapInterval());
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
		return deleted;
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

	public GLContext getGLContext() {
		return glContext;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public void setSizeChangeListener(GLWindowSizeChangeListener sizeChangeListener) {
		this.sizeChangeListener = sizeChangeListener;
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

	public void cleanup() {
		glfwDestroyWindow(windowPointer);
		windows.remove(this);

		deleted = true;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!deleted) Logger.log(Logger.WARNING, "Window not cleaned up: " + title + ", @" + Long.toHexString(windowPointer));
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
