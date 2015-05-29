package de.nerogar.noise.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GLContext;

import de.nerogar.noise.listener.GLWindowSizeChangeListener;
import de.nerogar.noise.log.Logger;

public class GLWindow implements IRenderTarget {

	private static List<GLWindow> windows;

	private long windowPointer;

	private String title;
	private int windowWidth, windowHeight;
	private int swapInterval;
	private boolean resizable;

	private GLContext glContext;

	private GLWindowSizeChangeListener sizeChangeListener;

	private InputHandler inputHandler;

	//holds references to callbacks, the gc will delete them otherwise
	private GLFWFramebufferSizeCallback frameBufferCallback;
	private GLFWCursorPosCallback cursorPosCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWCharModsCallback charModsCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback scrollCallback;

	private boolean deleted;

	/**
	 * Creates a new window for OpenGL. A new GLContext will be created.
	 * To share objects like textures or vertex buffers between windows, use the <code>parentWindow</code> parameter
	 * 
	 * @param title initial window Title
	 * @param width initial window width
	 * @param height initial window height
	 * @param resizable initial resizable status
	 * @param swapInterval initial swap interval (0 = unbound)
	 * @param monitor the monitor for fullscreen windows, null otherwise
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
		//glfwWindowHint(GLFW_DECORATED, GL_FALSE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, GL_FALSE);

		windowPointer = glfwCreateWindow(width, height, title, monitor == null ? NULL : monitor.getPointer(), parentWindow == null ? NULL : parentWindow.windowPointer);
		glfwSetInputMode(windowPointer, GLFW_STICKY_KEYS, GL_TRUE);
		inputHandler = new InputHandler(windowPointer);

		glfwMakeContextCurrent(windowPointer);
		glContext = GLContext.createFromCurrent();

		setSwapInterval(swapInterval);

		frameBufferCallback = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int newWidth, int newHeight) {
				long currentContext = glfwGetCurrentContext();
				glfwMakeContextCurrent(windowPointer);
				glViewport(0, 0, newWidth, newHeight);
				glfwMakeContextCurrent(currentContext);

				windowWidth = newWidth;
				windowHeight = newHeight;

				if (sizeChangeListener != null) sizeChangeListener.onChange(newWidth, newHeight);
			}
		};

		cursorPosCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				inputHandler.setCursorPosition(xpos, ypos);

				//glfwSetCursorPos(windowPointer, 0.0, 0.0);
			}
		};

		keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				inputHandler.addKeyboardKeyEvent(key, scancode, action, mods);
			}
		};

		charModsCallback = new GLFWCharModsCallback() {
			@Override
			public void invoke(long window, int codepoint, int mods) {
				inputHandler.addInputTextCodepoint(codepoint);
			}
		};

		mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				inputHandler.addMouseButtonEvent(button, action, mods);
			}
		};

		scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				inputHandler.setScrollWheelDelta(xoffset, yoffset);
			}
		};

		glfwSetFramebufferSizeCallback(windowPointer, frameBufferCallback);
		glfwSetCursorPosCallback(windowPointer, cursorPosCallback);
		glfwSetKeyCallback(windowPointer, keyCallback);
		glfwSetCharModsCallback(windowPointer, charModsCallback);
		glfwSetMouseButtonCallback(windowPointer, mouseButtonCallback);
		glfwSetScrollCallback(windowPointer, scrollCallback);

		glfwGetKey(windowPointer, GLFW_KEY_0);

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

	public void setSwapInterval(int swapInterval) {
		this.swapInterval = swapInterval;
		glfwSwapInterval(swapInterval);
	}

	public int getSwapInterval() {
		return swapInterval;
	}

	public void setShouldClose(boolean shouldClose) {
		glfwSetWindowShouldClose(windowPointer, shouldClose ? GL_TRUE : GL_FALSE);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowPointer) == GL_TRUE;
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

	public void setMouseHiding(boolean hide) {
		if (hide) {
			glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		} else {
			glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
	}

	/**
	 * Updates all events and swaps buffers on all windows.
	 */
	public static void updateAll() {
		for (GLWindow window : windows) {
			window.inputHandler.resetInputText();
			window.inputHandler.resetKeyboardKeyEvents();
			window.inputHandler.resetMouseButtonEvents();
			glfwSwapBuffers(window.windowPointer);
		}

		glfwPollEvents();
	}

	public void makeContextCurrent() {
		glfwMakeContextCurrent(windowPointer);
	}

	@Override
	public void bind() {
		glfwMakeContextCurrent(windowPointer);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, windowWidth, windowHeight);
	}

	public void cleanup() {
		glfwDestroyWindow(windowPointer);
		windows.remove(this);
	}

	@Override
	protected void finalize() throws Throwable {
		if (!deleted) Logger.log(Logger.WARNING, "Window not cleaned up. pointer: " + title + ", @" + Long.toHexString(windowPointer));
	}

	static {
		windows = new ArrayList<GLWindow>();
	}

}
