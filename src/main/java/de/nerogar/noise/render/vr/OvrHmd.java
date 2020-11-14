package de.nerogar.noise.render.vr;

import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.render.vr.IOvrTrackedDevice;
import org.lwjgl.openvr.VREventData;

public class OvrHmd implements IOvrTrackedDevice {

	private final int        ovrTrackedDeviceIndex;
	private final OvrContext ovrContext;

	private final Matrix4f gamePose;
	private final Matrix4f renderPose;

	public OvrHmd(int ovrTrackedDeviceIndex, OvrContext ovrContext) {
		this.ovrTrackedDeviceIndex = ovrTrackedDeviceIndex;
		this.ovrContext = ovrContext;
		gamePose = new Matrix4f();
		renderPose = new Matrix4f();
	}

	@Override
	public OvrTrackedDeviceType getType() {
		return OvrTrackedDeviceType.HMD;
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
	}

	@Override
	public void processEvent(int eventType, VREventData data) {
	}

}
