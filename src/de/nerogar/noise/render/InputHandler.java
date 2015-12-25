package de.nerogar.noise.render;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public final class InputHandler {

	public class KeyboardKeyEvent {
		public final int key;
		public final int scancode;
		public final int action;
		public final int mods;

		private boolean processed = false;

		public KeyboardKeyEvent(int key, int scancode, int action, int mods) {
			this.key = key;
			this.scancode = scancode;
			this.action = action;
			this.mods = mods;
		}

		public void setProcessed() {
			processed = true;
		}
	}

	public class MouseButtonEvent {
		public final int button;
		public final int action;
		public final int mods;

		private boolean processed = false;

		public MouseButtonEvent(int button, int action, int mods) {
			this.button = button;
			this.action = action;
			this.mods = mods;
		}

		public void setProcessed() {
			processed = true;
		}
	}

	private GLWindow window;
	private long windowPointer;

	private boolean ignoreMouseDelta;

	private double cursorPosX, cursorPosY;
	private double cursorDeltaX, cursorDeltaY;
	private double scrollDeltaX, scrollDeltaY;

	private StringBuilder inputText;

	private List<KeyboardKeyEvent> keyboardKeyEvents;
	private List<MouseButtonEvent> mouseButtonEvents;

	protected InputHandler(GLWindow window, long windowPointer) {
		this.window = window;
		this.windowPointer = windowPointer;

		ignoreMouseDelta = true;

		inputText = new StringBuilder();
		keyboardKeyEvents = new ArrayList<InputHandler.KeyboardKeyEvent>();
		mouseButtonEvents = new ArrayList<InputHandler.MouseButtonEvent>();
	}

	public GLWindow getWindow() {
		return window;
	}

	//---[mouse]---
	protected void setCursorPosition(double xpos, double ypos) {
		if (!ignoreMouseDelta) {
			cursorDeltaX = xpos - cursorPosX;
			cursorDeltaY = ypos - cursorPosY;
		} else {
			ignoreMouseDelta = false;
		}

		cursorPosX = xpos;
		cursorPosY = ypos;
	}

	protected void flagMouseDelta() {
		ignoreMouseDelta = true;

		cursorDeltaX = 0;
		cursorDeltaY = 0;
	}

	public float getCursorPosX() {
		return (float) cursorPosX;
	}

	public float getCursorPosY() {
		return (float) cursorPosY;
	}

	public float getCursorDeltaX() {
		return (float) cursorDeltaX;
	}

	public float getCursorDeltaY() {
		return (float) cursorDeltaY;
	}

	public float getScrollDeltaX() {
		return (float) scrollDeltaX;
	}

	public float getScrollDeltaY() {
		return (float) scrollDeltaY;
	}

	protected void addMouseButtonEvent(int button, int action, int mods) {
		mouseButtonEvents.add(new MouseButtonEvent(button, action, mods));
	}

	protected void resetMouseButtonEvents() {
		mouseButtonEvents.clear();
	}

	public boolean isButtonDown(int button) {
		return glfwGetMouseButton(windowPointer, button) == GLFW_PRESS;
	}

	/**
	 * @return A list of all MouseButtonEvents. Call setProcessed() on events you use.
	 */
	public List<MouseButtonEvent> getMouseButtonEvents() {
		List<MouseButtonEvent> events = new ArrayList<InputHandler.MouseButtonEvent>();

		for (MouseButtonEvent event : mouseButtonEvents) {
			if (!event.processed) events.add(event);
		}

		return events;
	}

	protected void setScrollWheelDelta(double scrollDeltaX, double scrollDeltaY) {
		this.scrollDeltaX = scrollDeltaX;
		this.scrollDeltaY = scrollDeltaY;
	}

	public void resetDeltaValues() {
		scrollDeltaX = 0;
		scrollDeltaY = 0;

		cursorDeltaX = 0;
		cursorDeltaY = 0;

	}

	//---[keyboard]---
	protected void addKeyboardKeyEvent(int key, int scancode, int action, int mods) {
		keyboardKeyEvents.add(new KeyboardKeyEvent(key, scancode, action, mods));
	}

	protected void resetKeyboardKeyEvents() {
		keyboardKeyEvents.clear();
	}

	/**
	 * @return A list of all KeyboardKeyEvent. Call setProcessed() on events you use.
	 */
	public List<KeyboardKeyEvent> getKeyboardKeyEvents() {
		List<KeyboardKeyEvent> events = new ArrayList<InputHandler.KeyboardKeyEvent>();

		for (KeyboardKeyEvent event : keyboardKeyEvents) {
			if (!event.processed) events.add(event);
		}

		return events;
	}

	public boolean isKeyDown(int key) {
		return glfwGetKey(windowPointer, key) == GLFW_PRESS;
	}

	protected void addInputTextCodepoint(int codePoint) {
		inputText.appendCodePoint(codePoint);
	}

	protected void resetInputText() {
		inputText = new StringBuilder();
	}

	public String getInputText() {
		String ret = inputText.toString();
		resetInputText();
		return ret;
	}

}
