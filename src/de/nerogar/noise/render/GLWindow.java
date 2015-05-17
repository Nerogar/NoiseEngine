package de.nerogar.noise.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GLContext;

import de.nerogar.noise.listener.GLWindowSizeChangeListener;

public class GLWindow implements IRenderTarget {

	private long windowHandle;

	private String title;
	private int width, height;
	private int swapInterval;
	private boolean resizable;

	private GLContext glContext;

	private GLWindowSizeChangeListener sizeChangeListener;

	/**
	 * @param title initial window Title
	 * @param width initial window width
	 * @param height initial window height
	 * @param resizable initial resizable status
	 * @param swapInterval initial swap interval (0 = unbound)
	 * @param monitor the monitor for fullscreen windows, null otherwise
	 */
	public GLWindow(String title, int width, int height, boolean resizable, int swapInterval, Monitor monitor) {
		this.title = title;
		this.resizable = resizable;

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
		//glfwWindowHint(GLFW_DECORATED, GL_FALSE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, GL_FALSE);

		windowHandle = glfwCreateWindow(width, height, title, monitor == null ? NULL : monitor.getPointer(), NULL);

		glfwMakeContextCurrent(windowHandle);
		glContext = GLContext.createFromCurrent();

		setSwapInterval(swapInterval);

		glfwSetFramebufferSizeCallback(windowHandle, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				if (sizeChangeListener != null) sizeChangeListener.onChange(width, height);
			}
		});

	}

	public GLWindow(String title, int width, int height, boolean resizable, int swapInterval) {
		this(title, width, height, resizable, swapInterval, null);
	}

	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;
		glfwSetWindowSize(windowHandle, width, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setSwapInterval(int swapInterval) {
		this.swapInterval = swapInterval;
		glfwSwapInterval(swapInterval);
	}

	public int getSwapInterval(){
		return swapInterval;
	}
	
	public void setShouldClose(boolean shouldClose) {
		glfwSetWindowShouldClose(windowHandle, shouldClose ? GL_TRUE : GL_FALSE);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandle) == GL_TRUE;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		glfwSetWindowTitle(windowHandle, title);
	}

	public boolean isResizable() {
		return resizable;
	}

	public GLContext getGLContext() {
		return glContext;
	}

	public void setSizeChangeListener(GLWindowSizeChangeListener sizeChangeListener) {
		this.sizeChangeListener = sizeChangeListener;
	}

	public void minimize() {
		glfwIconifyWindow(windowHandle);
	}

	public void show() {
		glfwRestoreWindow(windowHandle);
		glfwShowWindow(windowHandle);
	}

	public void enableMouseHiding(boolean hide) {
		if (hide) {
			glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		} else {
			glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		}
	}

	public void update() {
		long currentContext = glfwGetCurrentContext();

		glfwMakeContextCurrent(windowHandle);
		glfwPollEvents();

		glfwSwapBuffers(windowHandle);

		glfwMakeContextCurrent(currentContext);
	}

	@Override
	public void bind() {
		glfwMakeContextCurrent(windowHandle);
	}

}
