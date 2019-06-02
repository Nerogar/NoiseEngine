package de.nerogar.noise.render.vr;

import de.nerogar.noise.util.Matrix4f;
import org.lwjgl.openvr.VREventData;

import static org.lwjgl.openvr.VR.*;

public interface OvrTrackedDevice {

	class OvrTrackedDeviceType<T extends OvrTrackedDevice> {

		public static final OvrTrackedDeviceType<OvrInvalidTrackedDevice> INVALID            = new OvrTrackedDeviceType<>(ETrackedDeviceClass_TrackedDeviceClass_Invalid, OvrInvalidTrackedDevice.class);
		public static final OvrTrackedDeviceType<OvrHmd>                  HMD                = new OvrTrackedDeviceType<>(ETrackedDeviceClass_TrackedDeviceClass_HMD, OvrHmd.class);
		public static final OvrTrackedDeviceType<OvrController>           CONTROLLER         = new OvrTrackedDeviceType<>(ETrackedDeviceClass_TrackedDeviceClass_Controller, OvrController.class);
		public static final OvrTrackedDeviceType<OvrGenericTracker>       GENERIC_TRACKER    = new OvrTrackedDeviceType<>(ETrackedDeviceClass_TrackedDeviceClass_GenericTracker, OvrGenericTracker.class);
		public static final OvrTrackedDeviceType<OvrTrackingReference>    TRACKING_REFERENCE = new OvrTrackedDeviceType<>(ETrackedDeviceClass_TrackedDeviceClass_TrackingReference, OvrTrackingReference.class);
		public static final OvrTrackedDeviceType<OvrDisplayRedirect>      DISPLAY_REDIRECT   = new OvrTrackedDeviceType<>(ETrackedDeviceClass_TrackedDeviceClass_DisplayRedirect, OvrDisplayRedirect.class);

		public final int      ovrConstant;
		public final Class<T> deviceClass;

		OvrTrackedDeviceType(int ovrConstant, Class<T> deviceClass) {
			this.ovrConstant = ovrConstant;
			this.deviceClass = deviceClass;
		}

		private static OvrTrackedDeviceType[] values = {
				INVALID,
				HMD,
				CONTROLLER,
				GENERIC_TRACKER,
				TRACKING_REFERENCE,
				DISPLAY_REDIRECT,
		};

		public static OvrTrackedDeviceType[] values() {
			return values;
		}

		public static OvrTrackedDeviceType fromOvrConstant(int ovrConstant) {
			for (OvrTrackedDeviceType value : values()) {
				if (value.ovrConstant == ovrConstant) return value;
			}
			return INVALID;
		}
	}

	public static OvrTrackedDevice newDevice(int ovrTrackedDeviceTypeId, OvrContext ovrContext) {
		switch (ovrTrackedDeviceTypeId) {
			default:
			case ETrackedDeviceClass_TrackedDeviceClass_Invalid:
				return new OvrInvalidTrackedDevice(ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_HMD:
				return new OvrHmd(ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_Controller:
				return new OvrController(ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_GenericTracker:
				return new OvrGenericTracker(ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_TrackingReference:
				return new OvrTrackingReference(ovrContext);
			case ETrackedDeviceClass_TrackedDeviceClass_DisplayRedirect:
				return new OvrDisplayRedirect(ovrContext);
		}
	}

	public OvrTrackedDeviceType getType();

	public Matrix4f getGamePose();

	public Matrix4f getRenderPose();

	public void resetEvents();

	public void processEvent(int eventType, VREventData data);

}
