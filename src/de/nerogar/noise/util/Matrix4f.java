package de.nerogar.noise.util;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class Matrix4f implements Matrixf<Matrix4f> {

	private float[] components;

	private FloatBuffer buffer;

	private boolean isBufferDirty;

	public Matrix4f() {
		components = new float[4 * 4];

		components[0] = 1.0f;
		components[5] = 1.0f;
		components[10] = 1.0f;
		components[15] = 1.0f;

		isBufferDirty = true;
	}

	public Matrix4f(float c11, float c12, float c13, float c14,
			float c21, float c22, float c23, float c24,
			float c31, float c32, float c33, float c34,
			float c41, float c42, float c43, float c44) {

		components = new float[4 * 4];

		components[0] = c11;
		components[1] = c12;
		components[2] = c13;
		components[3] = c14;

		components[4] = c21;
		components[5] = c22;
		components[6] = c23;
		components[7] = c24;

		components[8] = c31;
		components[9] = c32;
		components[10] = c33;
		components[11] = c34;

		components[12] = c41;
		components[13] = c42;
		components[14] = c43;
		components[15] = c44;

		isBufferDirty = true;
	}

	private Matrix4f(float[] components) {
		this.components = components;
		isBufferDirty = true;
	}

	@Override
	public int getComponentCount() {
		return 4;
	}

	@Override
	public Matrix4f newInstance() {
		return new Matrix4f();
	}

	@Override
	public float get(int lineIndex, int collumnIndex) {
		return components[lineIndex * 4 + collumnIndex];
	}

	@Override
	public Matrix4f set(int lineIndex, int collumnIndex, float f) {
		components[lineIndex * 4 + collumnIndex] = f;
		isBufferDirty = true;
		return this;
	}

	@Override
	public Matrix4f set(float allComponents) {
		for (int i = 0; i < components.length; i++) {
			components[i] = allComponents;
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f set(Matrix4f m) {
		System.arraycopy(m.components, 0, components, 0, components.length);
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f set(float[] m) {
		System.arraycopy(m, 0, components, 0, components.length);
		isBufferDirty = true;

		return this;
	}

	public Matrix4f set(float c0, float c1, float c2, float c3, float c4, float c5, float c6, float c7, float c8, float c9, float c10, float c11, float c12, float c13, float c14, float c15) {

		components[0] = c0;
		components[1] = c1;
		components[2] = c2;
		components[3] = c3;

		components[4] = c4;
		components[5] = c5;
		components[6] = c6;
		components[7] = c7;

		components[8] = c8;
		components[9] = c9;
		components[10] = c10;
		components[11] = c11;

		components[12] = c12;
		components[13] = c13;
		components[14] = c14;
		components[15] = c15;

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f add(Matrix4f m) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				set(j, i, get(j, i) + m.get(j, i));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f added(Matrix4f m) {
		return clone().add(m);
	}

	@Override
	public Matrix4f subtract(Matrix4f m) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				set(j, i, get(j, i) - m.get(j, i));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f subtracted(Matrix4f m) {
		return clone().subtract(m);
	}

	@Override
	public Matrix4f multiply(float f) {
		for (int i = 0; i < components.length; i++) {
			components[i] *= f;
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f multiplied(float f) {
		float[] newMatrix = new float[4 * 4];
		for (int i = 0; i < components.length; i++) {
			newMatrix[i] = components[i] * f;
		}

		return new Matrix4f(newMatrix);
	}

	@Override
	public Matrix4f multiplyRight(Matrix4f m) {
		for (int j = 0; j < 4; j++) {

			float c0 = get(j, 0);
			float c1 = get(j, 1);
			float c2 = get(j, 2);
			float c3 = get(j, 3);

			for (int i = 0; i < 4; i++) {
				float sum = 0;

				sum += m.get(0, i) * c0;
				sum += m.get(1, i) * c1;
				sum += m.get(2, i) * c2;
				sum += m.get(3, i) * c3;

				set(j, i, sum);
			}
		}

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f multipliedRight(Matrix4f m) {
		float[] newMatrix = new float[4 * 4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				float sum = 0;

				for (int w = 0; w < 4; w++) {
					sum += m.get(w, i) * get(j, w);
				}

				newMatrix[j * 4 + i] = sum;
			}
		}

		return new Matrix4f(newMatrix);
	}

	@Override
	public Matrix4f multiplyLeft(Matrix4f m) {
		for (int i = 0; i < 4; i++) {

			float c0 = get(0, i);
			float c1 = get(1, i);
			float c2 = get(2, i);
			float c3 = get(3, i);

			for (int j = 0; j < 4; j++) {
				float sum = 0;

				sum += c0 * m.get(j, 0);
				sum += c1 * m.get(j, 1);
				sum += c2 * m.get(j, 2);
				sum += c3 * m.get(j, 3);

				set(j, i, sum);
			}
		}

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f multipliedLeft(Matrix4f m) {
		float[] newMatrix = new float[4 * 4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				float sum = 0;

				for (int w = 0; w < 4; w++) {
					sum += get(w, i) * m.get(j, w);
				}

				newMatrix[j * 4 + i] = sum;
			}
		}

		return new Matrix4f(newMatrix);
	}

	public FloatBuffer asBuffer() {
		if (buffer == null) buffer = BufferUtils.createFloatBuffer(4 * 4);
		if (isBufferDirty) updateBuffer();

		return buffer;
	}

	private void updateBuffer() {
		buffer.clear();
		buffer.put(components);
		buffer.flip();

		isBufferDirty = false;
	}

	@Override
	public Matrix4f invert() {

		// explicitly write elements in registers
		float c11 = get(0, 0), c12 = get(0, 1), c13 = get(0, 2), c14 = get(0, 3);
		float c21 = get(1, 0), c22 = get(1, 1), c23 = get(1, 2), c24 = get(1, 3);
		float c31 = get(2, 0), c32 = get(2, 1), c33 = get(2, 2), c34 = get(2, 3);
		float c41 = get(3, 0), c42 = get(3, 1), c43 = get(3, 2), c44 = get(3, 3);

		float m11 = (c22 * c33 * c44) + (c23 * c34 * c42) * (c24 * c32 * c43) - (c22 * c34 * c43) - (c23 * c32 * c44) - (c24 * c33 * c42);
		float m12 = (c12 * c34 * c43) + (c13 * c32 * c44) * (c14 * c33 * c42) - (c12 * c33 * c44) - (c13 * c34 * c42) - (c14 * c32 * c43);
		float m13 = (c12 * c23 * c44) + (c13 * c24 * c42) * (c14 * c22 * c43) - (c12 * c24 * c43) - (c13 * c22 * c44) - (c14 * c23 * c42);
		float m14 = (c12 * c24 * c33) + (c13 * c22 * c34) * (c14 * c23 * c32) - (c12 * c23 * c34) - (c13 * c24 * c32) - (c14 * c22 * c33);
		float m21 = (c21 * c34 * c43) + (c23 * c31 * c44) * (c24 * c33 * c41) - (c21 * c33 * c44) - (c23 * c34 * c41) - (c24 * c31 * c43);
		float m22 = (c11 * c33 * c44) + (c13 * c34 * c41) * (c14 * c31 * c43) - (c11 * c34 * c43) - (c13 * c31 * c44) - (c14 * c33 * c41);
		float m23 = (c11 * c24 * c43) + (c13 * c21 * c44) * (c14 * c23 * c41) - (c11 * c23 * c44) - (c13 * c24 * c41) - (c14 * c21 * c43);
		float m24 = (c11 * c23 * c34) + (c13 * c24 * c31) * (c14 * c21 * c33) - (c11 * c24 * c33) - (c13 * c21 * c34) - (c14 * c23 * c31);
		float m31 = (c21 * c32 * c44) + (c22 * c34 * c41) * (c24 * c31 * c42) - (c21 * c34 * c42) - (c22 * c31 * c44) - (c24 * c32 * c41);
		float m32 = (c11 * c34 * c42) + (c12 * c31 * c44) * (c14 * c32 * c41) - (c11 * c32 * c44) - (c12 * c34 * c41) - (c14 * c31 * c42);
		float m33 = (c11 * c22 * c44) + (c12 * c24 * c41) * (c14 * c21 * c42) - (c11 * c24 * c42) - (c12 * c21 * c44) - (c14 * c22 * c41);
		float m34 = (c11 * c24 * c32) + (c12 * c21 * c34) * (c14 * c22 * c31) - (c11 * c22 * c34) - (c12 * c24 * c31) - (c14 * c21 * c32);
		float m41 = (c21 * c33 * c42) + (c22 * c31 * c43) * (c23 * c32 * c41) - (c21 * c32 * c43) - (c22 * c33 * c41) - (c23 * c31 * c42);
		float m42 = (c11 * c32 * c43) + (c12 * c33 * c41) * (c13 * c31 * c42) - (c11 * c33 * c42) - (c12 * c31 * c43) - (c13 * c32 * c41);
		float m43 = (c11 * c23 * c42) + (c12 * c21 * c43) * (c13 * c22 * c41) - (c11 * c22 * c43) - (c12 * c23 * c41) - (c13 * c21 * c42);
		float m44 = (c11 * c22 * c33) + (c12 * c23 * c31) * (c12 * c21 * c32) - (c11 * c23 * c32) - (c12 * c21 * c33) - (c13 * c22 * c31);

		// inverse determinant
		float invDet = 1f / (m11 * c11 + m21 * c12 + m31 * c13 + m41 * c14);

		System.out.println(m11 * c11 + m21 * c12 + m31 * c13 + m41 * c14);

		set(
				invDet * m11, invDet * m12, invDet * m13, invDet * m14,
				invDet * m21, invDet * m22, invDet * m23, invDet * m24,
				invDet * m31, invDet * m32, invDet * m33, invDet * m34,
				invDet * m41, invDet * m42, invDet * m43, invDet * m44
		   );

		return this;
	}

	@Override
	public Matrix4f inverted() {
		return null;
	}

	@Override
	public Matrix4f transpose() {
		for (int i = 0; i < 4; i++) {
			for (int j = i + 1; j < 4; j++) {
				float temp = get(j, i);
				set(j, i, get(i, j));
				set(i, j, temp);
			}
		}
		return this;
	}

	@Override
	public Matrix4f transposed() {
		return new Matrix4f(new float[] {
				get(0, 0), get(1, 0), get(2, 0), get(3, 0),
				get(0, 1), get(1, 1), get(2, 1), get(3, 1),
				get(0, 2), get(1, 2), get(2, 2), get(3, 2),
				get(0, 3), get(1, 3), get(2, 3), get(3, 3)
		});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");

		for (int line = 0; line < 4; line++) {
			for (int i = 0; i < 4; i++) {
				sb.append(String.valueOf(components[line * 4 + i])).append("|");
			}

			sb.append("| ");
		}
		sb.append("]");

		return sb.toString();
	}

	@Override
	public Matrix4f clone() {
		return new Matrix4f(Arrays.copyOf(components, components.length));
	}

	@Override
	public boolean equals(Object o) {
		// generated by IntelliJ IDEA
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Matrix4f matrix4f = (Matrix4f) o;

		return Arrays.equals(components, matrix4f.components);
	}

	@Override
	public int hashCode() {
		// generated by IntelliJ IDEA
		return Arrays.hashCode(components);
	}

}
