package de.nerogar.noise.render.vr;

import de.nerogar.noise.Noise;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Vector2f;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSNodeRoot;
import de.nerogar.noise.serialization.NDSReader;
import de.nerogar.noise.util.Logger;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IReadonlyMatrix4f;
import de.nerogar.noiseInterface.math.IVector2f;
import de.nerogar.noiseInterface.math.IVector3f;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;

import java.io.FileNotFoundException;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openvr.VR.ETrackingUniverseOrigin_TrackingUniverseStanding;
import static org.lwjgl.openvr.VR.k_ulInvalidInputValueHandle;
import static org.lwjgl.openvr.VRInput.*;

public class OvrInputHandler {

	private final OvrContext ovrContext;

	private Map<String, Long> actionSetNames;
	private Map<String, Long> actionNames;

	private String[]                 activeActionSetNames;
	private VRActiveActionSet.Buffer activeActionSetsBuffer;
	private InputDigitalActionData   inputDigitalActionData;
	private InputPoseActionData      inputPoseActionData;
	private InputAnalogActionData    inputAnalogActionData;

	private Map<String, Boolean>   booleanStatesChanged;
	private Map<String, Boolean>   booleanStates;
	private Map<String, IMatrix4f> poseMatrices;
	private Map<String, IVector3f> analogStates;

	public OvrInputHandler(OvrContext ovrContext, String actionManifestPath) {
		this.ovrContext = ovrContext;
		this.actionSetNames = new HashMap<>();
		this.actionNames = new HashMap<>();
		this.poseMatrices = new HashMap<>();
		this.analogStates = new HashMap<>();

		inputDigitalActionData = InputDigitalActionData.calloc();
		inputPoseActionData = InputPoseActionData.calloc();
		inputAnalogActionData = InputAnalogActionData.calloc();
		booleanStates = new HashMap<>();
		booleanStatesChanged = new HashMap<>();

		readActionManifest(actionManifestPath);
	}

	private void readActionManifest(String fileName) {
		try {
			NDSFile ndsFile = NDSReader.readJsonFile(fileName);
			NDSNodeRoot data = ndsFile.getData();

			for (NDSNodeObject action : data.getObjectArray("actions")) {
				String actionName = action.getStringUTF8("name");
				String actionType = action.getStringUTF8("type");

				if (actionType.equals("boolean")) {
					booleanStates.put(actionName, false);
					booleanStatesChanged.put(actionName, false);
				}
			}

		} catch (FileNotFoundException e) {
			Noise.getLogger().log(Logger.ERROR, "Error while trying to read action ovr manifest file.");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		}
	}

	public void setActiveActionSet(String[] activeActionSetNames) {
		if (Arrays.equals(this.activeActionSetNames, activeActionSetNames)) {
			return;
		}

		this.activeActionSetNames = activeActionSetNames;

		if (activeActionSetsBuffer != null) {
			activeActionSetsBuffer.free();
			activeActionSetsBuffer = null;
		}

		activeActionSetsBuffer = VRActiveActionSet.calloc(activeActionSetNames.length);
		for (int i = 0; i < activeActionSetNames.length; i++) {
			VRActiveActionSet activeActionSet = activeActionSetsBuffer.get(i);
			activeActionSet.ulActionSet(getActionSetHandle(activeActionSetNames[i]));
		}
	}

	private long getActionSetHandle(String actionSetName) {
		if (!actionSetNames.containsKey(actionSetName)) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				LongBuffer actionSetHandleBuffer = stack.mallocLong(1);
				int error = VRInput_GetActionSetHandle(actionSetName, actionSetHandleBuffer);
				long actionSetHandle = actionSetHandleBuffer.get(0);
				actionSetNames.put(actionSetName, actionSetHandle);

				if (error != 0) {
					Noise.getLogger().log(Logger.ERROR, "Error while trying to get action set handle for ovr action set '" + actionSetName + "'. Error: " + error);
				}
			}
		}

		return actionSetNames.get(actionSetName);
	}

	private long getActionHandle(String actionName) {
		if (!actionNames.containsKey(actionName)) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				LongBuffer actionHandleBuffer = stack.mallocLong(1);
				int error = VRInput_GetActionHandle(actionName, actionHandleBuffer);
				long actionHandle = actionHandleBuffer.get(0);
				actionSetNames.put(actionName, actionHandle);

				if (error != 0) {
					Noise.getLogger().log(Logger.ERROR, "Error while trying to get action handle for ovr action '" + actionName + "'. Error: " + error);
				}
			}
		}

		return actionSetNames.get(actionName);
	}

	public void update() {
		if (activeActionSetsBuffer != null) {
			int updateActionStateError = VRInput_UpdateActionState(activeActionSetsBuffer, activeActionSetsBuffer.sizeof());
			if (updateActionStateError != 0) {
				Noise.getLogger().log(Logger.ERROR, "Error while trying to update action state for ovr actions. Error: " + updateActionStateError);
				return;
			}

			for (String actionName : booleanStatesChanged.keySet()) {
				long actionHandle = getActionHandle(actionName);
				int actionDataError = VRInput_GetDigitalActionData(actionHandle, inputDigitalActionData, k_ulInvalidInputValueHandle);
				if (actionDataError != 0) {
					Noise.getLogger().log(Logger.ERROR, "Error while trying to get digital state for ovr action '" + actionName + "'. Error: " + updateActionStateError);
					return;
				} else {
					booleanStates.put(actionName, inputDigitalActionData.bState() && inputDigitalActionData.bActive());
					booleanStatesChanged.put(actionName, inputDigitalActionData.bChanged() && inputDigitalActionData.bActive());
				}
			}
		}
	}

	public boolean isButtonDown(String actionName) {
		return booleanStates.get(actionName);
	}

	public boolean isButtonPressed(String actionName) {
		return booleanStates.get(actionName) && booleanStatesChanged.get(actionName);
	}

	public IReadonlyMatrix4f getPose(String actionName) {
		long actionHandle = getActionHandle(actionName);

		int error = VRInput.VRInput_GetPoseActionDataForNextFrame(actionHandle, ETrackingUniverseOrigin_TrackingUniverseStanding, inputPoseActionData, k_ulInvalidInputValueHandle);

		IMatrix4f poseMatrix = poseMatrices.computeIfAbsent(actionName, s -> new Matrix4f());
		TrackedDevicePose pose = inputPoseActionData.pose();
		HmdMatrix34 m = pose.mDeviceToAbsoluteTracking();

		if (error != 0) {
			Noise.getLogger().log(Logger.ERROR, "Error while trying to get pose state for ovr action '" + actionName + "'. Error: " + error);
		} else {
			poseMatrix.set(
					m.m(0), m.m(1), m.m(2), m.m(3),
					m.m(4), m.m(5), m.m(6), m.m(7),
					m.m(8), m.m(9), m.m(10), m.m(11),
					0, 0, 0, 1
			              );
		}

		return poseMatrix;
	}

	public IVector3f getAnalogState(String actionName) {
		long actionHandle = getActionHandle(actionName);

		int error = VRInput.VRInput_GetAnalogActionData(actionHandle, inputAnalogActionData, k_ulInvalidInputValueHandle);

		IVector3f analogState = analogStates.computeIfAbsent(actionName, s -> new Vector3f());

		if (error != 0) {
			Noise.getLogger().log(Logger.ERROR, "Error while trying to get pose state for ovr action '" + actionName + "'. Error: " + error);
		} else {
			analogState.setX(inputAnalogActionData.x());
			analogState.setY(inputAnalogActionData.y());
			analogState.setZ(inputAnalogActionData.z());
		}

		return analogState;
	}
}
