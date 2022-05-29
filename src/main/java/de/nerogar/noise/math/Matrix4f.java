package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IReadonlyMatrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class Matrix4f implements IMatrix4f {

	public static final IReadonlyMatrix4f UNIT_MATRIX = new Matrix4f();

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
	public float get(int lineIndex, int columnIndex) {
		return components[lineIndex * 4 + columnIndex];
	}

	@Override
	public Matrix4f set(int lineIndex, int columnIndex, float f) {
		components[lineIndex * 4 + columnIndex] = f;
		isBufferDirty = true;
		return this;
	}

	@Override
	public Matrix4f set(float allComponents) {
		Arrays.fill(components, allComponents);
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f set(IReadonlyMatrix4f m) {
		if (m instanceof Matrix4f) {
			System.arraycopy(((Matrix4f) m).components, 0, components, 0, components.length);
		} else {
			set(
					m.get(0, 0), m.get(0, 1), m.get(0, 2), m.get(0, 3),
					m.get(1, 0), m.get(1, 1), m.get(1, 2), m.get(1, 3),
					m.get(2, 0), m.get(2, 1), m.get(2, 2), m.get(2, 3),
					m.get(3, 0), m.get(3, 1), m.get(3, 2), m.get(3, 3)
			   );
		}

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f set(float[] m) {
		System.arraycopy(m, 0, components, 0, components.length);
		isBufferDirty = true;

		return this;
	}

	@Override
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
	public Matrix4f add(IReadonlyMatrix4f m) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				set(j, i, get(j, i) + m.get(j, i));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f added(IReadonlyMatrix4f m) {
		return clone().add(m);
	}

	@Override
	public Matrix4f subtract(IReadonlyMatrix4f m) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				set(j, i, get(j, i) - m.get(j, i));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f subtracted(IReadonlyMatrix4f m) {
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
	public Matrix4f multiplyRight(IReadonlyMatrix4f m) {
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
	public Matrix4f multipliedRight(IReadonlyMatrix4f m) {
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
	public Matrix4f multiplyLeft(IReadonlyMatrix4f m) {
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
	public Matrix4f multipliedLeft(IReadonlyMatrix4f m) {
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

	@Override
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

		// taken from stackoverflow.com/a/7596981

		// explicitly write elements in registers
		float c11 = get(0, 0), c12 = get(0, 1), c13 = get(0, 2), c14 = get(0, 3);
		float c21 = get(1, 0), c22 = get(1, 1), c23 = get(1, 2), c24 = get(1, 3);
		float c31 = get(2, 0), c32 = get(2, 1), c33 = get(2, 2), c34 = get(2, 3);
		float c41 = get(3, 0), c42 = get(3, 1), c43 = get(3, 2), c44 = get(3, 3);

		float s0 = c11 * c22 - c21 * c12;
		float s1 = c11 * c23 - c21 * c13;
		float s2 = c11 * c24 - c21 * c14;
		float s3 = c12 * c23 - c22 * c13;
		float s4 = c12 * c24 - c22 * c14;
		float s5 = c13 * c24 - c23 * c14;

		float c5 = c33 * c44 - c43 * c34;
		float c4 = c32 * c44 - c42 * c34;
		float c3 = c32 * c43 - c42 * c33;
		float c2 = c31 * c44 - c41 * c34;
		float c1 = c31 * c43 - c41 * c33;
		float c0 = c31 * c42 - c41 * c32;

		float invDet = 1f / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);

		float m11 = (c22 * c5 - c23 * c4 + c24 * c3) * invDet;
		float m12 = (-c12 * c5 + c13 * c4 - c14 * c3) * invDet;
		float m13 = (c42 * s5 - c43 * s4 + c44 * s3) * invDet;
		float m14 = (-c32 * s5 + c33 * s4 - c34 * s3) * invDet;
		float m21 = (-c21 * c5 + c23 * c2 - c24 * c1) * invDet;
		float m22 = (c11 * c5 - c13 * c2 + c14 * c1) * invDet;
		float m23 = (-c41 * s5 + c43 * s2 - c44 * s1) * invDet;
		float m24 = (c31 * s5 - c33 * s2 + c34 * s1) * invDet;
		float m31 = (c21 * c4 - c22 * c2 + c24 * c0) * invDet;
		float m32 = (-c11 * c4 + c12 * c2 - c14 * c0) * invDet;
		float m33 = (c41 * s4 - c42 * s2 + c44 * s0) * invDet;
		float m34 = (-c31 * s4 + c32 * s2 - c34 * s0) * invDet;
		float m41 = (-c21 * c3 + c22 * c1 - c23 * c0) * invDet;
		float m42 = (c11 * c3 - c12 * c1 + c13 * c0) * invDet;
		float m43 = (-c41 * s3 + c42 * s1 - c43 * s0) * invDet;
		float m44 = (c31 * s3 - c32 * s1 + c33 * s0) * invDet;

		set(
				m11, m12, m13, m14,
				m21, m22, m23, m24,
				m31, m32, m33, m34,
				m41, m42, m43, m44
		   );

		return this;
	}

	@Override
	public Matrix4f inverted() {
		return clone().invert();
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
				sb.append(components[line * 4 + i]).append("|");
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
