package de.nerogar.noise.util;

public class Matrix4fUtils {

	//@formatter:off
	public static void setUnitMatrix(Matrix4f m) {
		m.set(new float[] {
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f
		});
	}

	public static void setPositionMatrix(Matrix4f m, float x, float y, float z) {
		m.set(new float[] {
				1.0f, 0.0f, 0.0f, x,
				0.0f, 1.0f, 0.0f, y,
				0.0f, 0.0f, 1.0f, z,
				0.0f, 0.0f, 0.0f, 1.0f
		});
	}

	public static void setYawMatrix(Matrix4f m, float radiants) {
		m.set(new float[] {
				(float) Math.cos(-radiants),	0.0f,					(float) Math.sin(-radiants),	0.0f,
				0.0f,							1.0f,					0.0f,							0.0f,
				(float)							-Math.sin(-radiants),	0.0f,							(float) Math.cos(-radiants), 0.0f,
				0.0f,							0.0f,					0.0f,							1.0f
		});
	}

	public static void setPitchMatrix(Matrix4f m, float radiants) {
		m.set(new float[] {
				1.0f,	0.0f,							0.0f,							0.0f,
				0.0f,	(float) Math.cos(-radiants),	(float) -Math.sin(-radiants),	0.0f,
				0.0f,	(float) Math.sin(-radiants),	(float) Math.cos(-radiants),	0.0f,
				0.0f,	0.0f,							0.0f,							1.0f
		});
	}

	public static void setRollMatrix(Matrix4f m, float radiants) {
		m.set(new float[] {
				(float) Math.cos(-radiants),	(float) -Math.sin(-radiants),	0.0f,	0.0f,
				(float) Math.sin(-radiants),	(float) Math.cos(-radiants),	0.0f,	0.0f,
				0.0f,							0.0f,							1.0f,	0.0f,
				0.0f,							0.0f,							0.0f,	1.0f
		});
	}

	public static void setScaleMatrix(Matrix4f m, float scaleX, float scaleY, float scaleZ) {
		m.set(new float[] {
				scaleX,	0.0f,	0.0f,	0.0f,
				0.0f,	scaleY,	0.0f,	0.0f,
				0.0f,	0.0f,	scaleZ,	0.0f,
				0.0f,	0.0f,	0.0f,	1.0f
		});
	}

	public static void setOrthographicProjection(Matrix4f m, float left, float right, float top, float bottom, float near, float far) {
		m.set(new float[] {
				2f / (right-left),	0.0f,				0.0f,				-(right+left) / (right-left),
				0.0f,				2f / (top-bottom),	0.0f,				-(top+bottom) / (top-bottom),
				0.0f,				0.0f,				2f / (far-near),	-(far+near) / (far-near),
				0.0f,				0.0f,				0.0f,				1.0f
		});
	}
	//@formatter:on

}
