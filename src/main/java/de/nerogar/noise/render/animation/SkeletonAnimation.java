package de.nerogar.noise.render.animation;

import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Matrix4fUtils;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IReadonlyMatrix4f;
import de.nerogar.noiseInterface.math.IReadonlyVector3f;

public class SkeletonAnimation {

	private int frameCount;

	private float                 duration;
	private float[]               frameTimes;
	private IReadonlyMatrix4f[][] framePoses;
	private IReadonlyMatrix4f[][] globalFramePoses;
	private boolean               repeat;

	public SkeletonAnimation(int frameCount, float[] frameTimes, IReadonlyMatrix4f[][] framePoses, boolean repeat) {
		this.frameCount = frameCount;
		this.frameTimes = frameTimes;
		this.framePoses = framePoses;
		this.repeat = repeat;
	}

	public void setPose(Skeleton skeleton, SkeletonPose pose, float time) {
		if (repeat) {
			time %= frameTimes[frameCount - 1];
		}

		int frame = 0;
		float nextFrameFactor = 0;
		for (int i = 0; i < frameCount; i++) {
			if (frameTimes[i] <= time) {
				frame = i;
			} else {
				break;
			}
		}

		if (frame < frameCount - 1) {
			nextFrameFactor = (frameTimes[frame + 1] - time) / (frameTimes[frame + 1] - frameTimes[frame]);
		}

		IMatrix4f[] finalPose = pose.getPose();
		IReadonlyMatrix4f[] currentFramePose = framePoses[frame];
		IReadonlyMatrix4f[] nextFramePose = framePoses[frame + 1];
		int[] boneOrder = skeleton.getBoneOrder();

		IMatrix4f currentFrameTempMatrix = new Matrix4f();
		IMatrix4f nextFrameTempMatrix = new Matrix4f();

		for (int i = 0; i < currentFramePose.length; i++) {
			int bone = boneOrder[i];

			if (currentFramePose[bone] != null) {
				IReadonlyMatrix4f bindPose = skeleton.getBindPose()[bone];
				IReadonlyMatrix4f inverseBindPose = skeleton.getInverseBindPose()[bone];

				// bone transformation in local space
				currentFrameTempMatrix.set(currentFramePose[bone]);
				nextFrameTempMatrix.set(nextFramePose[bone]);

				// add transform to origin
				currentFrameTempMatrix.multiplyRight(inverseBindPose);
				nextFrameTempMatrix.multiplyRight(inverseBindPose);

				// add transform back to bone position
				currentFrameTempMatrix.multiplyLeft(bindPose);
				nextFrameTempMatrix.multiplyLeft(bindPose);

				// interpolate
				currentFrameTempMatrix.multiply(1 - nextFrameFactor);
				nextFrameTempMatrix.multiply(nextFrameFactor);
				currentFrameTempMatrix.add(nextFrameTempMatrix);

				// add parent transform
				if (skeleton.getParents()[bone] >= 0) {
					currentFrameTempMatrix.multiplyLeft(finalPose[skeleton.getParents()[bone]]);
				}

				// save to pose
				finalPose[bone].set(currentFrameTempMatrix);
			}
		}
	}

}
