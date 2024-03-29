package de.nerogar.noise.render.vr;

import de.nerogar.noise.Noise;
import de.nerogar.noise.input.Joystick;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Matrix4fUtils;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.camera.IVrCamera;
import de.nerogar.noise.render.camera.OvrCamera;
import de.nerogar.noise.util.Logger;
import de.nerogar.noise.util.NoiseResource;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.render.vr.IOvrTrackedDevice;
import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static de.nerogar.noise.render.vr.OvrTrackedDeviceType.HMD;
import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRSystem.*;

public class OvrContext extends NoiseResource {

	private int width;
	private int height;

	private GLWindow window;
	private OvrInputHandler ovrInputHandler;

	private org.lwjgl.openvr.Texture leftOvrEye;
	private org.lwjgl.openvr.Texture rightOvrEye;

	private HmdMatrix44 leftProjection;
	private HmdMatrix44 rightProjection;

	private OvrCamera camera;

	private TrackedDevicePose.Buffer trackedRenderPose;
	private TrackedDevicePose.Buffer trackedGamePose;
	private IOvrTrackedDevice[]      trackedDevices;
	private IOvrTrackedDevice[][]    trackedDevicesByType;
	private VREvent                  vrEvent;
	private VRControllerState        controllerState;

	public OvrContext(GLWindow window, String actionManifestPath) {
		super("OvrContext");
		this.window = window;
		this.window.setOvrContext(this);
		ovrInputHandler = new OvrInputHandler(this, actionManifestPath);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer errorCode = stack.mallocInt(1);

			int token = VR_InitInternal(errorCode, VR.EVRApplicationType_VRApplication_Scene);

			if (errorCode.get(0) == 0) {
				OpenVR.create(token);
				String pchActionManifestPath = Path.of(actionManifestPath).toAbsolutePath().toString();
				int error = VRInput.VRInput_SetActionManifestPath(pchActionManifestPath);

				if (error != 0) {
					Noise.getLogger().log(Logger.ERROR, "Error while trying to set ovr action manifest '" + actionManifestPath + "'. Error: " + error);
				}

				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				VRSystem_GetRecommendedRenderTargetSize(w, h);

				width = w.get(0);
				height = h.get(0);

				leftOvrEye = org.lwjgl.openvr.Texture.create();
				rightOvrEye = org.lwjgl.openvr.Texture.create();

				leftProjection = new HmdMatrix44(BufferUtils.createByteBuffer(16 * Float.BYTES));
				rightProjection = new HmdMatrix44(BufferUtils.createByteBuffer(16 * Float.BYTES));

				trackedRenderPose = TrackedDevicePose.create(k_unMaxTrackedDeviceCount);
				trackedGamePose = TrackedDevicePose.create(k_unMaxTrackedDeviceCount);
				initTrackedDevices();

				vrEvent = VREvent.create();
				controllerState = VRControllerState.create();

				camera = new OvrCamera(this);
			}
		}
	}

	public OvrInputHandler getOvrInputHandler() { return ovrInputHandler; }

	public int getWidth()                       { return width; }

	public int getHeight()                      { return height; }

	public IVrCamera getCamera()                { return camera; }

	private void initTrackedDevices() {
		trackedDevices = new IOvrTrackedDevice[k_unMaxTrackedDeviceCount];

		for (int i = 0; i < trackedDevices.length; i++) {
			trackedDevices[i] = IOvrTrackedDevice.newDevice(VRSystem_GetTrackedDeviceClass(i), i, this);
		}

		sortTrackedDevices();

		for (OvrController ovrController : getTrackedDevices(OvrTrackedDeviceType.CONTROLLER)) {
			window.getInputHandler().addJoystick(ovrController);
		}
	}

	private void sortTrackedDevices() {
		trackedDevicesByType = new IOvrTrackedDevice[OvrTrackedDeviceType.values().length][];
		for (int typeIndex = 0; typeIndex < trackedDevicesByType.length; typeIndex++) {
			int count = 0;
			for (IOvrTrackedDevice trackedDevice : trackedDevices) {
				if (trackedDevice.getType() == OvrTrackedDeviceType.values()[typeIndex]) {
					count++;
				}
			}

			trackedDevicesByType[typeIndex] = (IOvrTrackedDevice[]) Array.newInstance(OvrTrackedDeviceType.values()[typeIndex].deviceClass, count);

			int index = 0;
			for (IOvrTrackedDevice trackedDevice : trackedDevices) {
				if (trackedDevice.getType() == OvrTrackedDeviceType.values()[typeIndex]) {
					trackedDevicesByType[typeIndex][index] = trackedDevice;
					index++;
				}
			}
		}
	}

	public void update() {
		pollEvents();
		updateTracking();
		ovrInputHandler.update();

		VRCompositor.VRCompositor_PostPresentHandoff();
	}

	private void pollEvents() {
		for (IOvrTrackedDevice trackedDevice : trackedDevices) {
			trackedDevice.resetEvents();
		}

		while (VRSystem.VRSystem_PollNextEvent(vrEvent)) {
			int trackedDeviceIndex = vrEvent.trackedDeviceIndex();
			int eventType = vrEvent.eventType();

			switch (eventType) {
				case EVREventType_VREvent_TrackedDeviceActivated:
					trackedDevices[trackedDeviceIndex] = IOvrTrackedDevice.newDevice(VRSystem_GetTrackedDeviceClass(trackedDeviceIndex), trackedDeviceIndex, this);
					sortTrackedDevices();
					if (trackedDevices[trackedDeviceIndex] instanceof Joystick) {
						window.getInputHandler().addJoystick((Joystick) trackedDevices[trackedDeviceIndex]);
					}
					break;
				case EVREventType_VREvent_TrackedDeviceDeactivated:
					trackedDevices[trackedDeviceIndex] = IOvrTrackedDevice.newDevice(ETrackedDeviceClass_TrackedDeviceClass_Invalid, trackedDeviceIndex, this);
					sortTrackedDevices();
					if (trackedDevices[trackedDeviceIndex] instanceof Joystick) {
						window.getInputHandler().removeJoystick((Joystick) trackedDevices[trackedDeviceIndex]);
					}
					break;
				case EVREventType_VREvent_ButtonPress:
				case EVREventType_VREvent_ButtonUnpress:
				case EVREventType_VREvent_ButtonTouch:
				case EVREventType_VREvent_ButtonUntouch:
					if (trackedDeviceIndex != k_unTrackedDeviceIndexInvalid) {
						trackedDevices[trackedDeviceIndex].processEvent(eventType, vrEvent.data());
					}
					break;
				default:
					// todo: do other stuff with the event
					break;
			}
		}
	}

	private void updateTracking() {
		VRCompositor.VRCompositor_WaitGetPoses(trackedRenderPose, trackedGamePose);
		for (int i = 0; i < trackedDevices.length; i++) {
			HmdMatrix34 m = trackedRenderPose.get(i).mDeviceToAbsoluteTracking();

			trackedDevices[i].getRenderPose().set(
					m.m(0), m.m(1), m.m(2), m.m(3),
					m.m(4), m.m(5), m.m(6), m.m(7),
					m.m(8), m.m(9), m.m(10), m.m(11),
					0, 0, 0, 1
			                                     );

			trackedDevices[i].getRenderPose().set(
					m.m(0), m.m(1), m.m(2), m.m(3),
					m.m(4), m.m(5), m.m(6), m.m(7),
					m.m(8), m.m(9), m.m(10), m.m(11),
					0, 0, 0, 1
			                                     );
		}

		OvrHmd[] trackedDevices = getTrackedDevices(HMD);
		if (trackedDevices.length > 0) {
			IMatrix4f viewMatrix = trackedDevices[0].getRenderPose().clone().invert();

			if (camera.getBaseViewMatrix() != null) {
				viewMatrix.multiplyRight(camera.getBaseViewMatrix().inverted());
			}

			IMatrix4f leftViewMatrix = viewMatrix.multipliedLeft(Matrix4fUtils.getPositionMatrix(0.04f, 0, 0));
			IMatrix4f rightViewMatrix = viewMatrix.multipliedLeft(Matrix4fUtils.getPositionMatrix(-0.04f, 0, 0));

			IMatrix4f[] projectionMatrices = createProjectionMatrices();

			camera.getEyes()[0].manage(leftViewMatrix, projectionMatrices[0]);
			camera.getEyes()[1].manage(rightViewMatrix, projectionMatrices[1]);
		}
	}

	public IOvrTrackedDevice[] getTrackedDevices() {
		return trackedDevices;
	}

	public <T extends IOvrTrackedDevice> T[] getTrackedDevices(OvrTrackedDeviceType<T> ovrTrackedDeviceType) {
		return (T[]) trackedDevicesByType[ovrTrackedDeviceType.ovrConstant];
	}

	private IMatrix4f[] createProjectionMatrices() {

		VRSystem_GetProjectionMatrix(0, 0.1f, 1000, leftProjection);
		VRSystem_GetProjectionMatrix(1, 0.1f, 1000, rightProjection);
		return new Matrix4f[] {
				new Matrix4f(
						leftProjection.m(0), leftProjection.m(1), leftProjection.m(2), leftProjection.m(3),
						leftProjection.m(4), leftProjection.m(5), leftProjection.m(6), leftProjection.m(7),
						leftProjection.m(8), leftProjection.m(9), leftProjection.m(10), leftProjection.m(11),
						leftProjection.m(12), leftProjection.m(13), leftProjection.m(14), leftProjection.m(15)
				),
				new Matrix4f(
						rightProjection.m(0), rightProjection.m(1), rightProjection.m(2), rightProjection.m(3),
						rightProjection.m(4), rightProjection.m(5), rightProjection.m(6), rightProjection.m(7),
						rightProjection.m(8), rightProjection.m(9), rightProjection.m(10), rightProjection.m(11),
						rightProjection.m(12), rightProjection.m(13), rightProjection.m(14), rightProjection.m(15)
				),
		};

	}

	public void setTextures(Texture2D leftEyeTexture, Texture2D rightEyeTexture) {
		leftOvrEye.set(leftEyeTexture.getName(), ETextureType_TextureType_OpenGL, EColorSpace_ColorSpace_Auto);
		rightOvrEye.set(rightEyeTexture.getName(), ETextureType_TextureType_OpenGL, EColorSpace_ColorSpace_Auto);

		VRCompositor.VRCompositor_Submit(EVREye_Eye_Left, leftOvrEye, null, EVRSubmitFlags_Submit_Default);
		VRCompositor.VRCompositor_Submit(EVREye_Eye_Right, rightOvrEye, null, EVRSubmitFlags_Submit_Default);
	}

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;
		VR_ShutdownInternal();
		return true;
	}

}
