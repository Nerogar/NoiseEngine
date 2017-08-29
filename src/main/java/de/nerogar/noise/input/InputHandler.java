package de.nerogar.noise.input;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.util.Logger;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public final class InputHandler {

	private GLWindow window;
	private long     windowPointer;
	/** true if the mouse should be hidden */
	private boolean  hideMouse;
	/** true if the mouse is currently hidden */
	private boolean  mouseHidden;

	//holds references to callbacks, otherwise the gc will delete them
	private GLFWCursorPosCallback   cursorPosCallback;
	private GLFWKeyCallback         keyCallback;
	private GLFWCharModsCallback    charModsCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback      scrollCallback;
	private GLFWJoystickCallback    joystickCallback;
	private GLFWWindowFocusCallback windowFocusCallback;

	private boolean ignoreMouseDelta;

	private double cursorPosX, cursorPosY;
	private double cursorDeltaX, cursorDeltaY;
	private double scrollDeltaX, scrollDeltaY;

	private StringBuilder inputText;

	private List<KeyboardKeyEvent> keyboardKeyEvents;
	private List<MouseButtonEvent> mouseButtonEvents;

	private List<Joystick> joysticks;

	public InputHandler(GLWindow window, long windowPointer) {
		this.window = window;
		this.windowPointer = windowPointer;

		ignoreMouseDelta = true;

		inputText = new StringBuilder();
		keyboardKeyEvents = new ArrayList<>();
		mouseButtonEvents = new ArrayList<>();

		joysticks = new ArrayList<>();

		for (int i = GLFW_JOYSTICK_1; i <= GLFW_JOYSTICK_LAST; i++) {
			setJoystickActive(i, glfwJoystickPresent(i));
		}

		setCallbacks();
	}

	private void setCallbacks() {

		cursorPosCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (!hideMouse || mouseHidden) {
					setCursorPosition(xpos, (double) getWindow().getHeight() - ypos);
				}
			}
		};

		keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				addKeyboardKeyEvent(key, scancode, action, mods);
			}
		};

		charModsCallback = new GLFWCharModsCallback() {
			@Override
			public void invoke(long window, int codepoint, int mods) {
				addInputTextCodepoint(codepoint);
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

				addMouseButtonEvent(button, action, mods);
			}
		};
		scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				setScrollWheelDelta(xoffset, yoffset);
			}
		};

		joystickCallback = new GLFWJoystickCallback() {
			@Override
			public void invoke(int id, int status) {
				if (status == GLFW_CONNECTED) {
					setJoystickActive(id, true);
					Noise.getLogger().log(Logger.DEBUG, "Joystick " + id + " connected");
				} else {
					setJoystickActive(id, false);
					Noise.getLogger().log(Logger.DEBUG, "Joystick " + id + " disconnected");
				}
			}
		};

		windowFocusCallback = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				if (focused) {
					glfwSetInputMode(windowPointer, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
					mouseHidden = false;

					//fix mouse jumping
					flagMouseDelta();
				}
			}
		};

		glfwSetWindowFocusCallback(windowPointer, windowFocusCallback);
		glfwSetCursorPosCallback(windowPointer, cursorPosCallback);
		glfwSetKeyCallback(windowPointer, keyCallback);
		glfwSetCharModsCallback(windowPointer, charModsCallback);
		glfwSetMouseButtonCallback(windowPointer, mouseButtonCallback);
		glfwSetScrollCallback(windowPointer, scrollCallback);
		glfwSetJoystickCallback(joystickCallback);

	}

	public GLWindow getWindow() {
		return window;
	}

	/**
	 * called by {@link GLWindow#updateAll()} to indicate the next frame
	 */
	public void update() {
		resetInputText();
		resetKeyboardKeyEvents();
		resetMouseButtonEvents();
		resetDeltaValues();
		pollJoysticks();
	}

	//---[mouse]---
	private void setCursorPosition(double xpos, double ypos) {
		if (!ignoreMouseDelta) {
			cursorDeltaX = xpos - cursorPosX;
			cursorDeltaY = ypos - cursorPosY;
		} else {
			ignoreMouseDelta = false;
		}

		cursorPosX = xpos;
		cursorPosY = ypos;
	}

	private void flagMouseDelta() {
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

	private void addMouseButtonEvent(int button, int action, int mods) {
		mouseButtonEvents.add(new MouseButtonEvent(button, action, mods));
	}

	private void resetMouseButtonEvents() {
		mouseButtonEvents.clear();
	}

	public boolean isButtonDown(int button) {
		return glfwGetMouseButton(windowPointer, button) == GLFW_PRESS;
	}

	/**
	 * @return A list of all MouseButtonEvents. Call setProcessed() on events you use.
	 */
	public List<MouseButtonEvent> getMouseButtonEvents() {
		return mouseButtonEvents.stream()
				.filter((event) -> !event.processed)
				.collect(Collectors.toList());
	}

	private void setScrollWheelDelta(double scrollDeltaX, double scrollDeltaY) {
		this.scrollDeltaX = scrollDeltaX;
		this.scrollDeltaY = scrollDeltaY;
	}

	private void resetDeltaValues() {
		scrollDeltaX = 0;
		scrollDeltaY = 0;

		cursorDeltaX = 0;
		cursorDeltaY = 0;

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

	//---[keyboard]---
	private void addKeyboardKeyEvent(int key, int scancode, int action, int mods) {
		keyboardKeyEvents.add(new KeyboardKeyEvent(key, scancode, action, mods));
	}

	private void resetKeyboardKeyEvents() {
		keyboardKeyEvents.clear();
	}

	/**
	 * @return A list of all KeyboardKeyEvent. Call setProcessed() on events you use.
	 */
	public List<KeyboardKeyEvent> getKeyboardKeyEvents() {
		return keyboardKeyEvents.stream()
				.filter((event) -> !event.processed)
				.collect(Collectors.toList());
	}

	public boolean isKeyDown(int key) {
		return glfwGetKey(windowPointer, key) == GLFW_PRESS;
	}

	private void addInputTextCodepoint(int codePoint) {
		inputText.appendCodePoint(codePoint);
	}

	private void resetInputText() {
		inputText = new StringBuilder();
	}

	public String getInputText() {
		String ret = inputText.toString();
		resetInputText();
		return ret;
	}

	//---[joystick]---

	private void setJoystickActive(int id, boolean active) {
		if (active) {
			String joystickName = glfwGetJoystickName(id);

			Joystick joystick;

			if (Xbox360Controller.accept(joystickName)) {
				joystick = new Xbox360Controller(id, joystickName);
			} else {
				int buttonCount = glfwGetJoystickButtons(id).limit();
				int axisCount = glfwGetJoystickAxes(id).limit();
				joystick = new Joystick(id, joystickName, buttonCount, axisCount);
			}

			joysticks.add(joystick);

		} else {
			for (Joystick joystick : joysticks) {
				if (joystick.getId() == id) joystick.setDisconnected();
			}

			joysticks.removeIf((joystick) -> joystick.getId() == id);
		}
	}

	private void pollJoysticks() {
		joysticks.forEach(Joystick::poll);
	}

	public List<Joystick> getJoysticks(){
		return joysticks;
	}

	public boolean isJoystickButtonPressed(int button) {
		// TODO think of a better way to handle multiple joysticks
		// for now, if the button is pressed on one joystick, true is returned

		for (Joystick joystick : joysticks) {
			if (joystick.isButtonPressed(button)) return true;
		}

		return false;
	}

	public float getJoystickAxisStatus(int axis) {
		// TODO think of a better way to handle multiple joysticks
		// for now, only the first joystick is used

		if (joysticks.size() > 0) {
			return joysticks.get(0).getAxisStatus(axis);
		} else {
			return 0;
		}

	}

}
