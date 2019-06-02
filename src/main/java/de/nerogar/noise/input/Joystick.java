package de.nerogar.noise.input;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Joystick {

	public static final int NEGATIVE_AXIS_BIT = 0x10000;

	public static final int PRESS   = GLFW.GLFW_PRESS;
	public static final int RELEASE = GLFW.GLFW_RELEASE;

	private final String name;

	private boolean disconnected = false;

	protected boolean[] buttons;
	protected float[]   axes;

	protected List<JoystickEvent> events;

	protected Joystick(String name, int buttonCount, int axisCount) {
		this.name = name;

		buttons = new boolean[buttonCount];
		axes = new float[axisCount];

		events = new ArrayList<>();
	}

	protected void setDisconnected() {
		disconnected = true;
	}

	public String getName() {
		return name;
	}

	public List<JoystickEvent> getEvents() {
		return events.stream()
				.filter((event) -> !event.processed)
				.collect(Collectors.toList());
	}

	public boolean isButtonPressed(int button) {
		if (button < buttons.length) {
			return buttons[button];
		} else {
			return false;
		}
	}

	public float getAxisStatus(int axis) {
		float direction = 1;

		if ((axis & NEGATIVE_AXIS_BIT) != 0) {
			direction = -1;
			axis = axis & ~NEGATIVE_AXIS_BIT;
		}

		if (axis < axes.length) {
			return axes[axis] * direction;
		} else {
			return 0;
		}
	}

	public String getButtonName(int button) {
		return "button " + button;
	}

	public String getAxisName(int axis) {
		return "axis " + axis;
	}

}
