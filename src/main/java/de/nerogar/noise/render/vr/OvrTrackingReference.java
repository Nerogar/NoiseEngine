package de.nerogar.noise.render.vr;

import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.render.vr.IOvrTrackedDevice;
import org.lwjgl.openvr.VREventData;

public class OvrTrackingReference implements IOvrTrackedDevice {

	private final OvrContext ovrContext;

	private final IMatrix4f gamePose;
	private final IMatrix4f renderPose;

	public OvrTrackingReference(OvrContext ovrContext) {
		this.ovrContext = ovrContext;
		gamePose = new Matrix4f();
		renderPose = new Matrix4f();
	}

	@Override
	public OvrTrackedDeviceType getType() {
		return OvrTrackedDeviceType.TRACKING_REFERENCE;
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
	}

	@Override
	public void processEvent(int eventType, VREventData data) {
	}

}
