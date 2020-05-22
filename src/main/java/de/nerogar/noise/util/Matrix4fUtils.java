package de.nerogar.noise.util;

public final class Matrix4fUtils {

	private Matrix4fUtils() {
	}

	//@formatter:off
	public static void setPositionMatrix(Matrix4f m, float x, float y, float z) {
		m.set(
				1.0f, 0.0f, 0.0f, x,
				0.0f, 1.0f, 0.0f, y,
				0.0f, 0.0f, 1.0f, z,
				0.0f, 0.0f, 0.0f, 1.0f
		);
	}

	public static void setYawMatrix(Matrix4f m, float radiants) {
		m.set(
				(float) Math.cos(radiants),		0.0f,					(float) Math.sin(radiants),		0.0f,
				0.0f,							1.0f,					0.0f,							0.0f,
				(float)	-Math.sin(radiants),	0.0f,					(float) Math.cos(radiants),		0.0f,
				0.0f,							0.0f,					0.0f,							1.0f
		);
	}

	public static void setPitchMatrix(Matrix4f m, float radiants) {
		m.set(
				1.0f,	0.0f,							0.0f,							0.0f,
				0.0f,	(float) Math.cos(-radiants),	(float) Math.sin(-radiants),		0.0f,
				0.0f,	(float) -Math.sin(-radiants),	(float) Math.cos(-radiants),		0.0f,
				0.0f,	0.0f,							0.0f,							1.0f
		);
	}

	public static void setRollMatrix(Matrix4f m, float radiants) {
		m.set(
				(float) Math.cos(radiants),		(float) Math.sin(radiants),		0.0f,	0.0f,
				(float) -Math.sin(radiants),	(float) Math.cos(radiants),		0.0f,	0.0f,
				0.0f,							0.0f,							1.0f,	0.0f,
				0.0f,							0.0f,							0.0f,	1.0f
		);
	}

	public static void setScaleMatrix(Matrix4f m, float scaleX, float scaleY, float scaleZ) {
		m.set(
				scaleX,	0.0f,	0.0f,	0.0f,
				0.0f,	scaleY,	0.0f,	0.0f,
				0.0f,	0.0f,	scaleZ,	0.0f,
				0.0f,	0.0f,	0.0f,	1.0f
		);
	}

	public static void setOrthographicProjection(Matrix4f m, float left, float right, float top, float bottom, float near, float far) {
		m.set(
				2f / (right-left),	0.0f,				0.0f,				-(right+left) / (right-left),
				0.0f,				2f / (top-bottom),	0.0f,				-(top+bottom) / (top-bottom),
				0.0f,				0.0f,				2f / (far-near),	-(far+near) / (far-near),
				0.0f,				0.0f,				0.0f,				1.0f
		);
	}

	public static void setPerspectiveProjection(Matrix4f m, float fovDegree, float aspect, float near, float far) {
		float fovRadiants= (float) (fovDegree * Math.PI / 180.0);
		float f = (float) Math.tan(Math.PI * 0.5 - fovRadiants / 2.0f);

		m.set(
				f / aspect,		0.0f,		0.0f,							0.0f,
				0.0f,			f,			0.0f,							0.0f,
				0.0f,			0.0f,		(far + near) / (near - far),	(2 * far * near) / (near - far),
				0.0f,			0.0f,		-1.0f,							0.0f
		);
	}
	//@formatter:on

	public static Matrix4f getPositionMatrix(float x, float y, float z) {
		Matrix4f mat = new Matrix4f();
		setPositionMatrix(mat, x, y, z);
		return mat;
	}

	public static Matrix4f getYawMatrix(float radiants) {
		Matrix4f mat = new Matrix4f();
		setYawMatrix(mat, radiants);
		return mat;
	}

	public static Matrix4f getPitchMatrix(float radiants) {
		Matrix4f mat = new Matrix4f();
		setPitchMatrix(mat, radiants);
		return mat;
	}

	public static Matrix4f getRollMatrix(float radiants) {
		Matrix4f mat = new Matrix4f();
		setRollMatrix(mat, radiants);
		return mat;
	}

	public static Matrix4f getScaleMatrix(float scaleX, float scaleY, float scaleZ) {
		Matrix4f mat = new Matrix4f();
		setScaleMatrix(mat, scaleX, scaleY, scaleZ);
		return mat;
	}

	public static Matrix4f getOrthographicProjection(float left, float right, float top, float bottom, float near, float far) {
		Matrix4f mat = new Matrix4f();
		setOrthographicProjection(mat, left, right, top, bottom, near, far);
		return mat;
	}

	public static Matrix4f getPerspectiveProjection(float fovDegree, float aspect, float near, float far) {
		Matrix4f mat = new Matrix4f();
		setPerspectiveProjection(mat, fovDegree, aspect, near, far);
		return mat;
	}

}
