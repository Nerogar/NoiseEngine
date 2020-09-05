package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IMatrix4f;

public final class Matrix4fUtils {

	private Matrix4fUtils() {
	}

	//@formatter:off
	public static void setPositionMatrix(IMatrix4f m, float x, float y, float z) {
		m.set(
				1.0f,                         0.0f,                         0.0f,                         x,
				0.0f,                         1.0f,                         0.0f,                         y,
				0.0f,                         0.0f,                         1.0f,                         z,
				0.0f,                         0.0f,                         0.0f,                         1.0f
		);
	}

	public static void setYawMatrix(IMatrix4f m, float radians) {
		m.set(
				(float) Math.cos(radians),    0.0f,                         (float) Math.sin(radians),    0.0f,
				0.0f,                         1.0f,                         0.0f,                         0.0f,
				(float)	-Math.sin(radians),   0.0f,                         (float) Math.cos(radians),    0.0f,
				0.0f,                         0.0f,                         0.0f,                         1.0f
		);
	}

	public static void setPitchMatrix(IMatrix4f m, float radians) {
		m.set(
				1.0f,                         0.0f,                         0.0f,                         0.0f,
				0.0f,                         (float) Math.cos(-radians),   (float) Math.sin(-radians),   0.0f,
				0.0f,                         (float) -Math.sin(-radians),  (float) Math.cos(-radians),   0.0f,
				0.0f,                         0.0f,                         0.0f,	                      1.0f
		);
	}

	public static void setRollMatrix(IMatrix4f m, float radians) {
		m.set(
				(float) Math.cos(radians),    (float) Math.sin(radians),    0.0f,                         0.0f,
				(float) -Math.sin(radians),   (float) Math.cos(radians),    0.0f,                         0.0f,
				0.0f,                         0.0f,                         1.0f,                         0.0f,
				0.0f,                         0.0f,                         0.0f,                         1.0f
		);
	}

	public static void setScaleMatrix(IMatrix4f m, float scaleX, float scaleY, float scaleZ) {
		m.set(
				scaleX,                       0.0f,                         0.0f,                         0.0f,
				0.0f,                         scaleY,                       0.0f,                         0.0f,
				0.0f,                         0.0f,                         scaleZ,                       0.0f,
				0.0f,                         0.0f,                         0.0f,                         1.0f
		);
	}

	public static void setOrthographicProjection(IMatrix4f m, float left, float right, float top, float bottom, float near, float far) {
		m.set(
				2f / (right-left),            0.0f,                         0.0f,                         -(right+left) / (right-left),
				0.0f,                         2f / (top-bottom),            0.0f,                         -(top+bottom) / (top-bottom),
				0.0f,                         0.0f,                         2f / (far-near),              -(far+near) / (far-near),
				0.0f,                         0.0f,                         0.0f,                         1.0f
		);
	}

	public static void setPerspectiveProjection(IMatrix4f m, float fovDegree, float aspect, float near, float far) {
		float fovRadians= (float) (fovDegree * Math.PI / 180.0);
		float f = (float) Math.tan(Math.PI * 0.5 - fovRadians / 2.0f);

		m.set(
				f / aspect,                   0.0f,                         0.0f,                         0.0f,
				0.0f,                         f,                            0.0f,                         0.0f,
				0.0f,                         0.0f,                         (far + near) / (near - far),  (2 * far * near) / (near - far),
				0.0f,                         0.0f,                         -1.0f,                        0.0f
		);
	}
	//@formatter:on

	public static IMatrix4f getPositionMatrix(float x, float y, float z) {
		Matrix4f mat = new Matrix4f();
		setPositionMatrix(mat, x, y, z);
		return mat;
	}

	public static IMatrix4f getYawMatrix(float radians) {
		Matrix4f mat = new Matrix4f();
		setYawMatrix(mat, radians);
		return mat;
	}

	public static IMatrix4f getPitchMatrix(float radians) {
		Matrix4f mat = new Matrix4f();
		setPitchMatrix(mat, radians);
		return mat;
	}

	public static IMatrix4f getRollMatrix(float radians) {
		Matrix4f mat = new Matrix4f();
		setRollMatrix(mat, radians);
		return mat;
	}

	public static IMatrix4f getScaleMatrix(float scaleX, float scaleY, float scaleZ) {
		Matrix4f mat = new Matrix4f();
		setScaleMatrix(mat, scaleX, scaleY, scaleZ);
		return mat;
	}

	public static IMatrix4f getOrthographicProjection(float left, float right, float top, float bottom, float near, float far) {
		Matrix4f mat = new Matrix4f();
		setOrthographicProjection(mat, left, right, top, bottom, near, far);
		return mat;
	}

	public static IMatrix4f getPerspectiveProjection(float fovDegree, float aspect, float near, float far) {
		Matrix4f mat = new Matrix4f();
		setPerspectiveProjection(mat, fovDegree, aspect, near, far);
		return mat;
	}

}
