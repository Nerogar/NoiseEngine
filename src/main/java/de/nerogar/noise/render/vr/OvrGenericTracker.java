package de.nerogar.noise.render.vr;

import de.nerogar.noise.util.Matrix4f;
import org.lwjgl.openvr.VREventData;

public class OvrGenericTracker implements OvrTrackedDevice {

	private final OvrContext ovrContext;

	private final Matrix4f gamePose;
	private final Matrix4f renderPose;

	public OvrGenericTracker(OvrContext ovrContext) {
		this.ovrContext = ovrContext;
		gamePose = new Matrix4f();
		renderPose = new Matrix4f();
	}

	@Override
	public OvrTrackedDeviceType getType() {
		return OvrTrackedDeviceType.GENERIC_TRACKER;
	}

	@Override
	public Matrix4f getGamePose() {
		return gamePose;
	}

	@Override
	public Matrix4f getRenderPose() {
		return renderPose;
	}

	@Override
	public void resetEvents() {
	}

	@Override
	public void processEvent(int eventType, VREventData data) {
	}

}
