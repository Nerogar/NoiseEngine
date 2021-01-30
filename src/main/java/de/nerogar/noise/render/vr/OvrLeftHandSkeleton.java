package de.nerogar.noise.render.vr;

import de.nerogar.noise.render.animation.Skeleton;
import de.nerogar.noiseInterface.math.IReadonlyMatrix4f;

public class OvrLeftHandSkeleton extends Skeleton {

	public static final int BONE_INDEX_INVALID           = -1;
	public static final int BONE_INDEX_ROOT              = 0;
	public static final int BONE_INDEX_WRIST             = 1;
	public static final int BONE_INDEX_THUMB_0           = 2;
	public static final int BONE_INDEX_THUMB_1           = 3;
	public static final int BONE_INDEX_THUMB_2           = 4;
	public static final int BONE_INDEX_THUMB_END         = 5;
	public static final int BONE_INDEX_INDEX_FINGER_0    = 6;
	public static final int BONE_INDEX_INDEX_FINGER_1    = 7;
	public static final int BONE_INDEX_INDEX_FINGER_2    = 8;
	public static final int BONE_INDEX_INDEX_FINGER_3    = 9;
	public static final int BONE_INDEX_INDEX_FINGER_END  = 10;
	public static final int BONE_INDEX_MIDDLE_FINGER_0   = 11;
	public static final int BONE_INDEX_MIDDLE_FINGER_1   = 12;
	public static final int BONE_INDEX_MIDDLE_FINGER_2   = 13;
	public static final int BONE_INDEX_MIDDLE_FINGER_3   = 14;
	public static final int BONE_INDEX_MIDDLE_FINGER_END = 15;
	public static final int BONE_INDEX_RING_FINGER_0     = 16;
	public static final int BONE_INDEX_RING_FINGER_1     = 17;
	public static final int BONE_INDEX_RING_FINGER_2     = 18;
	public static final int BONE_INDEX_RING_FINGER_3     = 19;
	public static final int BONE_INDEX_RING_FINGER_END   = 20;
	public static final int BONE_INDEX_PINKY_FINGER_0    = 21;
	public static final int BONE_INDEX_PINKY_FINGER_1    = 22;
	public static final int BONE_INDEX_PINKY_FINGER_2    = 23;
	public static final int BONE_INDEX_PINKY_FINGER_3    = 24;
	public static final int BONE_INDEX_PINKY_FINGER_END  = 25;
	public static final int BONE_INDEX_THUMB_AUX         = 26;
	public static final int BONE_INDEX_INDEX_FINGER_AUX  = 27;
	public static final int BONE_INDEX_MIDDLE_FINGER_AUX = 28;
	public static final int BONE_INDEX_RING_FINGER_AUX   = 29;
	public static final int BONE_INDEX_PINKY_FINGER_AUX  = 30;
	public static final int BONE_COUNT                   = 31;

	public static final String BONE_NAME_ROOT              = "Root";
	public static final String BONE_NAME_WRIST             = "wrist_l";
	public static final String BONE_NAME_THUMB_0           = "finger_thumb_0_l";
	public static final String BONE_NAME_THUMB_1           = "finger_thumb_1_l";
	public static final String BONE_NAME_THUMB_2           = "finger_thumb_2_l";
	public static final String BONE_NAME_THUMB_END         = "finger_thumb_l_end";
	public static final String BONE_NAME_INDEX_FINGER_0    = "finger_index_meta_l";
	public static final String BONE_NAME_INDEX_FINGER_1    = "finger_index_0_l";
	public static final String BONE_NAME_INDEX_FINGER_2    = "finger_index_1_l";
	public static final String BONE_NAME_INDEX_FINGER_3    = "finger_index_2_l";
	public static final String BONE_NAME_INDEX_FINGER_END  = "finger_index_l_end";
	public static final String BONE_NAME_MIDDLE_FINGER_0   = "finger_middle_meta_l";
	public static final String BONE_NAME_MIDDLE_FINGER_1   = "finger_middle_0_l";
	public static final String BONE_NAME_MIDDLE_FINGER_2   = "finger_middle_1_l";
	public static final String BONE_NAME_MIDDLE_FINGER_3   = "finger_middle_2_l";
	public static final String BONE_NAME_MIDDLE_FINGER_END = "finger_middle_l_end";
	public static final String BONE_NAME_RING_FINGER_0     = "finger_ring_meta_l";
	public static final String BONE_NAME_RING_FINGER_1     = "finger_ring_0_l";
	public static final String BONE_NAME_RING_FINGER_2     = "finger_ring_1_l";
	public static final String BONE_NAME_RING_FINGER_3     = "finger_ring_2_l";
	public static final String BONE_NAME_RING_FINGER_END   = "finger_ring_l_end";
	public static final String BONE_NAME_PINKY_FINGER_0    = "finger_pinky_meta_l";
	public static final String BONE_NAME_PINKY_FINGER_1    = "finger_pinky_0_l";
	public static final String BONE_NAME_PINKY_FINGER_2    = "finger_pinky_1_l";
	public static final String BONE_NAME_PINKY_FINGER_3    = "finger_pinky_2_l";
	public static final String BONE_NAME_PINKY_FINGER_END  = "finger_pinky_l_end";
	public static final String BONE_NAME_THUMB_AUX         = "finger_thumb_l_aux";
	public static final String BONE_NAME_INDEX_FINGER_AUX  = "finger_index_l_aux";
	public static final String BONE_NAME_MIDDLE_FINGER_AUX = "finger_middle_l_aux";
	public static final String BONE_NAME_RING_FINGER_AUX   = "finger_ring_l_aux";
	public static final String BONE_NAME_PINKY_FINGER_AUX  = "finger_pinky_l_aux";

	public OvrLeftHandSkeleton(IReadonlyMatrix4f[] bindPose) {
		super(
				new String[] {
						BONE_NAME_ROOT,
						BONE_NAME_WRIST,
						BONE_NAME_THUMB_0,
						BONE_NAME_THUMB_1,
						BONE_NAME_THUMB_2,
						BONE_NAME_THUMB_END,
						BONE_NAME_INDEX_FINGER_0,
						BONE_NAME_INDEX_FINGER_1,
						BONE_NAME_INDEX_FINGER_2,
						BONE_NAME_INDEX_FINGER_3,
						BONE_NAME_INDEX_FINGER_END,
						BONE_NAME_MIDDLE_FINGER_0,
						BONE_NAME_MIDDLE_FINGER_1,
						BONE_NAME_MIDDLE_FINGER_2,
						BONE_NAME_MIDDLE_FINGER_3,
						BONE_NAME_MIDDLE_FINGER_END,
						BONE_NAME_RING_FINGER_0,
						BONE_NAME_RING_FINGER_1,
						BONE_NAME_RING_FINGER_2,
						BONE_NAME_RING_FINGER_3,
						BONE_NAME_RING_FINGER_END,
						BONE_NAME_PINKY_FINGER_0,
						BONE_NAME_PINKY_FINGER_1,
						BONE_NAME_PINKY_FINGER_2,
						BONE_NAME_PINKY_FINGER_3,
						BONE_NAME_PINKY_FINGER_END,
						BONE_NAME_THUMB_AUX,
						BONE_NAME_INDEX_FINGER_AUX,
						BONE_NAME_MIDDLE_FINGER_AUX,
						BONE_NAME_RING_FINGER_AUX,
						BONE_NAME_PINKY_FINGER_AUX,
				},
				new int[] {
						BONE_INDEX_INVALID,          // BONE_INDEX_ROOT
						BONE_INDEX_ROOT,             // BONE_INDEX_WRIST
						BONE_INDEX_WRIST,            // BONE_INDEX_THUMB_0
						BONE_INDEX_THUMB_0,          // BONE_INDEX_THUMB_1
						BONE_INDEX_THUMB_1,          // BONE_INDEX_THUMB_2
						BONE_INDEX_THUMB_2,          // BONE_INDEX_THUMB_END
						BONE_INDEX_WRIST,            // BONE_INDEX_INDEX_FINGER_0
						BONE_INDEX_INDEX_FINGER_0,   // BONE_INDEX_INDEX_FINGER_1
						BONE_INDEX_INDEX_FINGER_1,   // BONE_INDEX_INDEX_FINGER_2
						BONE_INDEX_INDEX_FINGER_2,   // BONE_INDEX_INDEX_FINGER_3
						BONE_INDEX_INDEX_FINGER_3,   // BONE_INDEX_INDEX_FINGER_END
						BONE_INDEX_WRIST,            // BONE_INDEX_MIDDLE_FINGER_0
						BONE_INDEX_MIDDLE_FINGER_0,  // BONE_INDEX_MIDDLE_FINGER_1
						BONE_INDEX_MIDDLE_FINGER_1,  // BONE_INDEX_MIDDLE_FINGER_2
						BONE_INDEX_MIDDLE_FINGER_2,  // BONE_INDEX_MIDDLE_FINGER_3
						BONE_INDEX_MIDDLE_FINGER_3,  // BONE_INDEX_MIDDLE_FINGER_END
						BONE_INDEX_WRIST,            // BONE_INDEX_RING_FINGER_0
						BONE_INDEX_RING_FINGER_0,    // BONE_INDEX_RING_FINGER_1
						BONE_INDEX_RING_FINGER_1,    // BONE_INDEX_RING_FINGER_2
						BONE_INDEX_RING_FINGER_2,    // BONE_INDEX_RING_FINGER_3
						BONE_INDEX_RING_FINGER_3,    // BONE_INDEX_RING_FINGER_END
						BONE_INDEX_WRIST,            // BONE_INDEX_PINKY_FINGER_0
						BONE_INDEX_PINKY_FINGER_0,   // BONE_INDEX_PINKY_FINGER_1
						BONE_INDEX_PINKY_FINGER_1,   // BONE_INDEX_PINKY_FINGER_2
						BONE_INDEX_PINKY_FINGER_2,   // BONE_INDEX_PINKY_FINGER_3
						BONE_INDEX_PINKY_FINGER_3,   // BONE_INDEX_PINKY_FINGER_END
						BONE_INDEX_ROOT,             // BONE_INDEX_THUMB_AUX
						BONE_INDEX_ROOT,             // BONE_INDEX_INDEX_FINGER_AUX
						BONE_INDEX_ROOT,             // BONE_INDEX_MIDDLE_FINGER_AUX
						BONE_INDEX_ROOT,             // BONE_INDEX_RING_FINGER_AUX
						BONE_INDEX_ROOT,             // BONE_INDEX_PINKY_FINGER_AUX
				},
				bindPose
		     );
	}

}
