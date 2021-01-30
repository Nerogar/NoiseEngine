package de.nerogar.noise.render.animation;

import de.nerogar.noiseInterface.math.IReadonlyMatrix4f;

import java.util.HashMap;
import java.util.Map;

public class Skeleton {

	private String[]             boneNames;
	private Map<String, Integer> boneNamesMap;
	private int[]                parents;
	private int[]                boneOrder;
	private IReadonlyMatrix4f[]  bindPose;
	private IReadonlyMatrix4f[]  inverseBindPose;

	public Skeleton(String[] boneNames, int[] parents, IReadonlyMatrix4f[] bindPose) {
		this.boneNames = boneNames;
		this.parents = parents;
		this.bindPose = bindPose;

		boneNamesMap = new HashMap<>();
		for (int i = 0; i < boneNames.length; i++) {
			boneNamesMap.put(boneNames[i], i);
		}

		createBoneOrder();
		createInverseBindPose();
	}

	/**
	 * Creates an array containing the order of bones starting from root bones.
	 * This order can be used to update animations in a way that guarantees that
	 * the parent bone of each bone is updated before the bone itself.
	 */
	private void createBoneOrder() {
		boneOrder = new int[getBoneCount()];

		int maxIndex = 0;
		boolean[] processed = new boolean[getBoneCount()];
		boolean allProcessed = false;

		while (!allProcessed) {
			allProcessed = true;
			for (int bone = 0; bone < getBoneCount(); bone++) {
				if ((parents[bone] < 0 || processed[parents[bone]]) && !processed[bone]) {
					boneOrder[maxIndex] = bone;
					processed[bone] = true;
					maxIndex++;
				} else if (!processed[bone]) {
					allProcessed = false;
				}
			}
		}
	}

	private void createInverseBindPose() {
		inverseBindPose = new IReadonlyMatrix4f[bindPose.length];
		for (int i = 0; i < bindPose.length; i++) {
			inverseBindPose[i] = bindPose[i].inverted();
		}
	}

	public int getBoneCount()                       { return boneNames.length; }

	public String[] getBoneNames()                  { return boneNames; }

	public int getBoneIndex(String name)            { return boneNamesMap.get(name); }

	public int[] getParents()                       { return parents; }

	public int[] getBoneOrder()                     { return boneOrder; }

	public IReadonlyMatrix4f[] getBindPose()        { return bindPose; }

	public IReadonlyMatrix4f[] getInverseBindPose() { return inverseBindPose; }
}
