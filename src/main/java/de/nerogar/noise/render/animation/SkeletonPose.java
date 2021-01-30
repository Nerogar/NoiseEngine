package de.nerogar.noise.render.animation;

import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noiseInterface.math.IMatrix4f;

public class SkeletonPose {

	// represents a pose of a skeleton.
	// this can be a single frame of an animation applied to a skeleton

	private IMatrix4f[] pose;

	public SkeletonPose(int boneCount) {
		pose = new IMatrix4f[boneCount];
		for (int i = 0; i < pose.length; i++) {
			pose[i] = new Matrix4f();
		}
	}

	public IMatrix4f[] getPose() {
		return pose;
	}

}
