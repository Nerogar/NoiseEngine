package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IReadonlyQuaternion;

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

	public static void setRotationMatrix(IMatrix4f m, float yawRadians, float pitchRadians, float rollRadians) {
		float sy = (float) Math.sin(yawRadians);
		float cy = (float) Math.cos(yawRadians);
		float sp = (float) Math.sin(-pitchRadians);
		float cp = (float) Math.cos(-pitchRadians);
		float sr = (float) Math.sin(rollRadians);
		float cr = (float) Math.cos(rollRadians);

		m.set(
				cy*cr + sy*sp*sr,             cy*sr - sy*sp*cr,             sy*cp,                        0,
				-cp*sr,                       cp*cr,                        sp,                           0,
				-sy*cr + cy*sp*sr,            -sy*sr - cy*sp*cr,            cy*cp,                        0,
				0,                            0,                            0,                            1
		     );
	}

	public static void setRotationMatrixByUnitQuaternion(IMatrix4f m, IReadonlyQuaternion quaternion) {
		float x = quaternion.getX();
		float y = quaternion.getY();
		float z = quaternion.getZ();
		float w = quaternion.getW();

		m.set(
				1.0f - 2.0f*y*y - 2.0f*z*z,   2.0f*x*y - 2.0f*z*w,          2.0f*x*z + 2.0f*y*w,          0.0f,
				2.0f*x*y + 2.0f*z*w,          1.0f - 2.0f*x*x - 2.0f*z*z,   2.0f*y*z - 2.0f*x*w,          0.0f,
				2.0f*x*z - 2.0f*y*w,          2.0f*y*z + 2.0f*x*w,          1.0f - 2.0f*x*x - 2.0f*y*y,   0.0f,
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

	public static IMatrix4f getRotationMatrix(float yawRadians, float pitchRadians, float rollRadians) {
		Matrix4f mat = new Matrix4f();
		setRotationMatrix(mat, yawRadians, pitchRadians, rollRadians);
		return mat;
	}

	public static IMatrix4f getRotationMatrixByUnitQuaternion(IReadonlyQuaternion quaternion) {
		Matrix4f mat = new Matrix4f();
		setRotationMatrixByUnitQuaternion(mat, quaternion);
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
