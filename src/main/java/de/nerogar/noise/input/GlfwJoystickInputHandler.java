package de.nerogar.noise.input;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.util.Logger;
import org.lwjgl.glfw.GLFWJoystickCallback;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwJoystickInputHandler {

	private InputHandler inputHandler;

	private Set<GlfwJoystick> joysticks;

	private GLFWJoystickCallback joystickCallback;

	public GlfwJoystickInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;

		joysticks = new HashSet<>();

		for (int i = GLFW_JOYSTICK_1; i <= GLFW_JOYSTICK_LAST; i++) {
			setJoystickActive(i, glfwJoystickPresent(i));
		}

		setCallbacks();
	}

	private void setCallbacks() {

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

		glfwSetJoystickCallback(joystickCallback);

	}

	/**
	 * called by {@link GLWindow#updateAll()} to indicate the next frame
	 */
	public void update() {
		pollJoysticks();
	}

	private void setJoystickActive(int id, boolean active) {
		if (active) {
			String joystickName = glfwGetJoystickName(id);

			GlfwJoystick joystick;

			if (Xbox360Controller.accept(joystickName)) {
				joystick = new Xbox360Controller(id, joystickName);
			} else {
				int buttonCount = glfwGetJoystickButtons(id).limit();
				int axisCount = glfwGetJoystickAxes(id).limit();
				joystick = new GlfwJoystick(id, joystickName, buttonCount, axisCount);
			}

			joysticks.add(joystick);
			inputHandler.addJoystick(joystick);

		} else {
			for (GlfwJoystick joystick : joysticks) {
				if (joystick.getGlfwId() == id) {
					joystick.setDisconnected();
					inputHandler.removeJoystick(joystick);
				}
			}

			joysticks.removeIf((joystick) -> joystick.getGlfwId() == id);
		}
	}

	private void pollJoysticks() {
		joysticks.forEach(GlfwJoystick::poll);
	}

}
