package de.nerogar.noiseInterface.render.vr;

import de.nerogar.noise.render.vr.*;
import de.nerogar.noiseInterface.math.IMatrix4f;
import org.lwjgl.openvr.VREventData;

import static org.lwjgl.openvr.VR.*;

public interface IOvrTrackedDevice {

	static IOvrTrackedDevice newDevice(int ovrTrackedDeviceTypeId, int ovrTrackedDeviceIndex, OvrContext ovrContext) {
		switch (ovrTrackedDeviceTypeId) {
			default:
			case ETrackedDeviceClass_TrackedDeviceClass_Invalid:
				return new OvrInvalidTrackedDevice(ovrTrackedDeviceIndex, ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_HMD:
				return new OvrHmd(ovrTrackedDeviceIndex, ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_Controller:
				return new OvrController(ovrTrackedDeviceIndex, ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_GenericTracker:
				return new OvrGenericTracker(ovrTrackedDeviceIndex, ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_TrackingReference:
				return new OvrTrackingReference(ovrTrackedDeviceIndex, ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_DisplayRedirect:
				return new OvrDisplayRedirect(ovrTrackedDeviceIndex, ovrContext);
		}
	}

	OvrTrackedDeviceType getType();

	int getTrackedDeviceIndex();

	IMatrix4f getGamePose();

	IMatrix4f getRenderPose();

	void resetEvents();

	void processEvent(int eventType, VREventData data);

}
