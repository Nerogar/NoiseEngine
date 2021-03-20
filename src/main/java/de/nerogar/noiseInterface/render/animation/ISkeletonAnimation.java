package de.nerogar.noiseInterface.render.animation;

import de.nerogar.noise.render.animation.Skeleton;
import de.nerogar.noise.render.animation.SkeletonPose;

public interface ISkeletonAnimation {

	void setPose(Skeleton skeleton, SkeletonPose pose, float time);
}
