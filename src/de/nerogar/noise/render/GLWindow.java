package de.nerogar.noise.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GLContext;

import de.nerogar.noise.listener.GLWindowSizeChangeListener;

public class GLWindow implements IRenderTarget {

	private long windowPointer;

	private String title;
	private int width, height;
	private int swapInterval;
	private boolean resizable;

	private GLContext glContext;

	private GLWindowSizeChangeListener sizeChangeListener;

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
		this.title = title;
		this.resizable = resizable;

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
		//glfwWindowHint(GLFW_DECORATED, GL_FALSE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, GL_FALSE);

		windowPointer = glfwCreateWindow(width, height, title, monitor == null ? NULL : monitor.getPointer(), parentWindow == null ? NULL : parentWindow.windowPointer);

		glfwMakeContextCurrent(windowPointer);
		glContext = GLContext.createFromCurrent();

		setSwapInterval(swapInterval);

		glfwSetFramebufferSizeCallback(windowPointer, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				long currentContext = glfwGetCurrentContext();
				glfwMakeContextCurrent(windowPointer);
				glViewport(0, 0, width, height);
				glfwMakeContextCurrent(currentContext);

				updateResolution(width, height);

				if (sizeChangeListener != null) sizeChangeListener.onChange(width, height);
			}
		});

	}

	private void updateResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;
		glfwSetWindowSize(windowPointer, width, height);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		glfwSetWindowTitle(windowPointer, title);
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
		glfwIconifyWindow(windowPointer);
	}

	public void show() {
		glfwRestoreWindow(windowPointer);
		glfwShowWindow(windowPointer);
	}

	public void enableMouseHiding(boolean hide) {
		if (hide) {
			glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		} else {
			glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		}
	}

	public void update() {
		long currentContext = glfwGetCurrentContext();

		glfwMakeContextCurrent(windowPointer);
		glfwPollEvents();

		glfwSwapBuffers(windowPointer);

		glfwMakeContextCurrent(currentContext);
	}

	public void makeContextCurrent(){
		glfwMakeContextCurrent(windowPointer);
	}
	
	@Override
	public void bind() {
		glfwMakeContextCurrent(windowPointer);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

}
