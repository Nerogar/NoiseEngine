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

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

public class GLWindow implements IRenderTarget {

	private static List<GLWindow> windows;

	private long windowPointer;

	private String title;
	private int windowWidth, windowHeight;
	private int swapInterval;
	private boolean resizable;
	/**true if the mouse should be hidden*/
	private boolean hideMouse;
	/**true if the mouse is currently hidden*/
	private boolean mouseHidden;

	private GLContext glContext;

	private GLWindowSizeChangeListener sizeChangeListener;

	private InputHandler inputHandler;

	//holds references to callbacks, otherwise the gc will delete them
	private GLFWWindowFocusCallback windowFocusCallback;
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
		glfwWindowHint(GLFW_AUTO_ICONIFY, GL_FALSE);

		windowPointer = glfwCreateWindow(width, height, title, monitor == null ? NULL : monitor.getPointer(), parentWindow == null ? NULL : parentWindow.windowPointer);
		glfwSetInputMode(windowPointer, GLFW_STICKY_KEYS, GL_TRUE);
		inputHandler = new InputHandler(this, windowPointer);

		glfwMakeContextCurrent(windowPointer);
		glContext = GLContext.createFromCurrent();

		setSwapInterval(swapInterval > 0 ? swapInterval : 0);

		windowFocusCallback = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, int focused) {
				if (focused == GL_FALSE) {
					glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
					mouseHidden = false;
				}
			}
		};

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
				if (!hideMouse || mouseHidden) {
					inputHandler.setCursorPosition(xpos, (double) getHeight() - ypos);
				}
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
				if (hideMouse && !mouseHidden) {
					glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
					mouseHidden = true;

					return;
				}

				inputHandler.addMouseButtonEvent(button, action, mods);
			}
		};

		scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				inputHandler.setScrollWheelDelta(xoffset, yoffset);
			}
		};

		glfwSetWindowFocusCallback(windowPointer, windowFocusCallback);
		glfwSetFramebufferSizeCallback(windowPointer, frameBufferCallback);
		glfwSetCursorPosCallback(windowPointer, cursorPosCallback);
		glfwSetKeyCallback(windowPointer, keyCallback);
		glfwSetCharModsCallback(windowPointer, charModsCallback);
		glfwSetMouseButtonCallback(windowPointer, mouseButtonCallback);
		glfwSetScrollCallback(windowPointer, scrollCallback);

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
		this.hideMouse = hide;
		this.mouseHidden = hide;

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
			window.inputHandler.resetDeltaValues();

			glfwSwapBuffers(window.windowPointer);
		}

		glfwPollEvents();
		Noise.getRessourceProfiler().reset();
		Noise.getDebugWindow().update();
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
