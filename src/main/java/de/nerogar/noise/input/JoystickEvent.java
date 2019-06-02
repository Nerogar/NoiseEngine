package de.nerogar.noise.input;

import org.lwjgl.glfw.GLFW;

public class JoystickEvent {

	public static final int BUTTON = 0;
	public static final int AXIS   = 1;

	/** either {@link JoystickEvent#BUTTON JoystickEvent.BUTTON} or {@link JoystickEvent#AXIS JoystickEvent.AXIS} */
	public final int type;
	/** the id of the button or axis */
	public final int id;
	/** either {@link GLFW#GLFW_PRESS} or {@link GLFW#GLFW_RELEASE} */
	public final int action;

	protected boolean processed = false;

	public JoystickEvent(int type, int id, int action) {
		this.type = type;
		this.id = id;
		this.action = action;
	}

	public void setProcessed() {
		processed = true;
	}

}
