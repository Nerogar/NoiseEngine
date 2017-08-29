package de.nerogar.noise.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public class Joystick {

	public static final int NEGATIVE_AXIS_BIT = 0x10000;

	private final int    id;
	private final String name;

	private boolean disconnected = false;

	private boolean[] buttons;
	private float[]   axes;

	private List<JoystickEvent> events;

	protected Joystick(int id, String name, int buttonCount, int axisCount) {
		this.id = id;
		this.name = name;

		buttons = new boolean[buttonCount];
		axes = new float[axisCount];

		events = new ArrayList<>();
	}

	protected void setDisconnected() {
		disconnected = true;
	}

	protected void poll() {
		FloatBuffer axesBuffer = glfwGetJoystickAxes(id);
		ByteBuffer buttonsBuffer = glfwGetJoystickButtons(id);

		events.clear();

		for (int button = 0; button < buttonsBuffer.limit(); button++) {
			boolean oldState = isButtonPressed(button);
			buttons[button] = buttonsBuffer.get(button) == GLFW_PRESS;
			boolean newState = isButtonPressed(button);

			if (oldState != newState) {
				events.add(new JoystickEvent(JoystickEvent.BUTTON, button, newState ? GLFW_PRESS : GLFW_RELEASE));
			}
		}

		for (int axis = 0; axis < axesBuffer.limit(); axis++) {
			float oldState = getAxisStatus(axis);
			axes[axis] = axesBuffer.get(axis);
			float newState = getAxisStatus(axis);

			if (oldState < 0.5 && newState >= 0.5) {
				events.add(new JoystickEvent(JoystickEvent.AXIS, axis, GLFW_PRESS));
			} else if (oldState >= 0.5 && newState < 0.5) {
				events.add(new JoystickEvent(JoystickEvent.AXIS, axis, GLFW_RELEASE));
			} else if (oldState > -0.5 && newState <= -0.5) {
				events.add(new JoystickEvent(JoystickEvent.AXIS, axis | NEGATIVE_AXIS_BIT, GLFW_PRESS));
			} else if (oldState <= -0.5 && newState > -0.5) {
				events.add(new JoystickEvent(JoystickEvent.AXIS, axis | NEGATIVE_AXIS_BIT, GLFW_RELEASE));
			}
		}
	}

	protected int getId() {
		return id;
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
