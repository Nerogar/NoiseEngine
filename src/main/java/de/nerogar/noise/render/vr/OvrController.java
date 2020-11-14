package de.nerogar.noise.render.vr;

import de.nerogar.noise.input.Joystick;
import de.nerogar.noise.input.JoystickEvent;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.render.vr.IOvrTrackedDevice;
import org.lwjgl.openvr.VREventData;

import static org.lwjgl.openvr.VR.*;

public class OvrController extends Joystick implements IOvrTrackedDevice {

	private final int        ovrTrackedDeviceIndex;
	private final OvrContext ovrContext;

	private final IMatrix4f gamePose;
	private final IMatrix4f renderPose;

	public OvrController(int ovrTrackedDeviceIndex, OvrContext ovrContext) {
		super("generic OvrController", 0, 0);
		this.ovrTrackedDeviceIndex = ovrTrackedDeviceIndex;
		this.ovrContext = ovrContext;
		gamePose = new Matrix4f();
		renderPose = new Matrix4f();
	}

	@Override
	public OvrTrackedDeviceType getType() {
		return OvrTrackedDeviceType.CONTROLLER;
	}

	@Override
	public int getTrackedDeviceIndex() {
		return ovrTrackedDeviceIndex;
	}

	@Override
	public IMatrix4f getGamePose() {
		return gamePose;
	}

	@Override
	public IMatrix4f getRenderPose() {
		return renderPose;
	}

	@Override
	public void resetEvents() {
		events.clear();
	}

	@Override
	public void processEvent(int eventType, VREventData data) {
		/*int button;
		switch (eventType) {
			case EVREventType_VREvent_ButtonPress:
				button = data.controller().button();
				resizeButtons(button + 1);
				buttons[button] = true;
				events.add(new JoystickEvent(JoystickEvent.BUTTON, button, PRESS));
				break;
			case EVREventType_VREvent_ButtonUnpress:
				button = data.controller().button();
				resizeButtons(button + 1);
				buttons[button] = false;
				events.add(new JoystickEvent(JoystickEvent.BUTTON, button, RELEASE));
				break;
			case EVREventType_VREvent_ButtonTouch:
				button = data.controller().button();
				resizeButtons(button + 1);
				buttons[button] = true;
				events.add(new JoystickEvent(JoystickEvent.BUTTON, button, TOUCH));
				break;
			case EVREventType_VREvent_ButtonUntouch:
				button = data.controller().button();
				resizeButtons(button + 1);
				buttons[button] = false;
				events.add(new JoystickEvent(JoystickEvent.BUTTON, button, UNTOUCH));
				break;
			default:
				break;
		}*/
	}

	private void resizeButtons(int newSize) {
		if (newSize > buttons.length) {
			boolean[] buttonsNew = new boolean[newSize];
			System.arraycopy(buttons, 0, buttonsNew, 0, buttons.length);
			buttons = buttonsNew;
		}
	}

	private void resizeAxes(int newSize) {
		if (newSize > buttons.length) {
			float[] axesNew = new float[newSize];
			System.arraycopy(buttons, 0, axesNew, 0, axes.length);
			axes = axesNew;
		}
	}

}
