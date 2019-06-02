package de.nerogar.noise.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwJoystick extends Joystick {

	private int glfwId;

	protected GlfwJoystick(int glfwId, String name, int buttonCount, int axisCount) {
		super(name, buttonCount, axisCount);
		this.glfwId = glfwId;
	}

	public int getGlfwId() {
		return glfwId;
	}

	protected void poll() {
		FloatBuffer axesBuffer = glfwGetJoystickAxes(glfwId);
		ByteBuffer buttonsBuffer = glfwGetJoystickButtons(glfwId);

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

}
