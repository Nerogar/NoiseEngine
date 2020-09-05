package de.nerogar.noise.math;

import de.nerogar.noiseInterface.math.IMatrix3f;
import de.nerogar.noiseInterface.math.IReadonlyMatrix3f;
import de.nerogar.noiseInterface.math.IReadonlyVector3f;
import de.nerogar.noiseInterface.math.IVector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class Matrix3f implements IMatrix3f {

	private float[] components;

	private FloatBuffer buffer;

	private boolean isBufferDirty;

	public Matrix3f() {
		components = new float[3 * 3];

		components[0] = 1.0f;
		components[4] = 1.0f;
		components[8] = 1.0f;

		isBufferDirty = true;
	}

	public Matrix3f(float c0, float c1, float c2, float c3, float c4, float c5, float c6, float c7, float c8) {
		components = new float[3 * 3];

		components[0] = c0;
		components[1] = c1;
		components[2] = c2;

		components[3] = c3;
		components[4] = c4;
		components[5] = c5;

		components[6] = c6;
		components[7] = c7;
		components[8] = c8;

		isBufferDirty = true;
	}

	private Matrix3f(float[] components) {
		this.components = components;
		isBufferDirty = true;
	}

	@Override
	public int getComponentCount() {
		return 3;
	}

	@Override
	public Matrix3f newInstance() {
		return new Matrix3f();
	}

	@Override
	public float get(int lineIndex, int columnIndex) {
		return components[lineIndex * 3 + columnIndex];
	}

	@Override
	public Matrix3f set(int lineIndex, int columnIndex, float f) {
		components[lineIndex * 3 + columnIndex] = f;
		isBufferDirty = true;
		return this;
	}

	@Override
	public Matrix3f set(float allComponents) {
		Arrays.fill(components, allComponents);
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f set(IReadonlyMatrix3f m) {
		if (m instanceof Matrix3f) {
			System.arraycopy(((Matrix3f) m).components, 0, components, 0, components.length);
		} else {
			set(
					m.get(0, 0), m.get(0, 1), m.get(0, 2),
					m.get(1, 0), m.get(1, 1), m.get(1, 2),
					m.get(2, 0), m.get(2, 1), m.get(2, 2)
			   );
		}

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f set(float[] m) {
		System.arraycopy(m, 0, components, 0, components.length);
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f set(float c0, float c1, float c2, float c3, float c4, float c5, float c6, float c7, float c8) {

		components[0] = c0;
		components[1] = c1;
		components[2] = c2;

		components[3] = c3;
		components[4] = c4;
		components[5] = c5;

		components[6] = c6;
		components[7] = c7;
		components[8] = c8;

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f add(IReadonlyMatrix3f m) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				set(j, i, get(j, i) + m.get(j, i));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f added(IReadonlyMatrix3f m) {
		return clone().add(m);
	}

	@Override
	public Matrix3f subtract(IReadonlyMatrix3f m) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				set(j, i, get(j, i) - m.get(j, i));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f subtracted(IReadonlyMatrix3f m) {
		return clone().subtract(m);
	}

	@Override
	public Matrix3f multiply(float f) {
		for (int i = 0; i < components.length; i++) {
			components[i] *= f;
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f multiplied(float f) {
		float[] newMatrix = new float[3 * 3];
		for (int i = 0; i < components.length; i++) {
			newMatrix[i] = components[i] * f;
		}

		return new Matrix3f(newMatrix);
	}

	@Override
	public Matrix3f multiplyRight(IReadonlyMatrix3f m) {
		for (int j = 0; j < 3; j++) {

			float c0 = get(j, 0);
			float c1 = get(j, 1);
			float c2 = get(j, 2);

			for (int i = 0; i < 3; i++) {
				float sum = 0;

				sum += m.get(0, i) * c0;
				sum += m.get(1, i) * c1;
				sum += m.get(2, i) * c2;

				set(j, i, sum);
			}
		}

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f multipliedRight(IReadonlyMatrix3f m) {
		float[] newMatrix = new float[4 * 4];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				float sum = 0;

				for (int w = 0; w < 3; w++) {
					sum += m.get(w, i) * get(j, w);
				}

				newMatrix[j * 3 + i] = sum;
			}
		}

		return new Matrix3f(newMatrix);
	}

	@Override
	public Matrix3f multiplyLeft(IReadonlyMatrix3f m) {
		for (int i = 0; i < 3; i++) {

			float c0 = get(0, i);
			float c1 = get(1, i);
			float c2 = get(2, i);

			for (int j = 0; j < 3; j++) {
				float sum = 0;

				sum += c0 * m.get(j, 0);
				sum += c1 * m.get(j, 1);
				sum += c2 * m.get(j, 2);

				set(j, i, sum);
			}
		}

		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix3f multipliedLeft(IReadonlyMatrix3f m) {
		float[] newMatrix = new float[4 * 4];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				float sum = 0;

				for (int w = 0; w < 3; w++) {
					sum += get(w, i) * m.get(j, w);
				}

				newMatrix[j * 3 + i] = sum;
			}
		}

		return new Matrix3f(newMatrix);
	}

	@Override
	public IVector3f multiply(IVector3f v) {
		float x = v.getX();
		float y = v.getY();
		float z = v.getZ();

		v.set(
				components[0] * x + components[1] * y + components[2] * z,
				components[3] * x + components[4] * y + components[5] * z,
				components[6] * x + components[7] * y + components[8] * z
		     );

		return v;
	}

	@Override
	public Vector3f multiplied(IReadonlyVector3f v) {
		float x = v.getX();
		float y = v.getY();
		float z = v.getZ();

		float newX = components[0] * x + components[1] * y + components[2] * z;
		float newY = components[3] * x + components[4] * y + components[5] * z;
		float newZ = components[6] * x + components[7] * y + components[8] * z;

		return new Vector3f(newX, newY, newZ);
	}

	@Override
	public FloatBuffer asBuffer() {
		if (buffer == null) buffer = BufferUtils.createFloatBuffer(3 * 3);
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
	public Matrix3f invert() {

		// explicitly write elements in registers
		float c11 = get(0, 0), c12 = get(0, 1), c13 = get(0, 2);
		float c21 = get(1, 0), c22 = get(1, 1), c23 = get(1, 2);
		float c31 = get(2, 0), c32 = get(2, 1), c33 = get(2, 2);

		// pre calculate some values that are used more than once
		float a11 = c22 * c33 - c23 * c32;
		float a21 = c23 * c31 - c21 * c33;
		float a31 = c21 * c32 - c22 * c31;

		// inverse determinant
		float invDet = 1f / (c11 * a11 + c12 * a21 + c13 * a31);

		float m11 = invDet * a11;
		float m12 = invDet * (c13 * c32 - c12 * c33);
		float m13 = invDet * (c12 * c23 - c13 * c22);

		float m21 = invDet * a21;
		float m22 = invDet * (c11 * c33 - c13 * c31);
		float m23 = invDet * (c13 * c21 - c11 * c23);

		float m31 = invDet * a31;
		float m32 = invDet * (c12 * c31 - c11 * c32);
		float m33 = invDet * (c11 * c22 - c12 * c21);

		set(
				m11, m12, m13,
				m21, m22, m23,
				m31, m32, m33
		   );

		return this;
	}

	@Override
	public Matrix3f inverted() {

		// explicitly write elements in registers
		float c11 = get(0, 0), c12 = get(0, 1), c13 = get(0, 2);
		float c21 = get(1, 0), c22 = get(1, 1), c23 = get(1, 2);
		float c31 = get(2, 0), c32 = get(2, 1), c33 = get(2, 2);

		// pre calculate some values that are used more than once
		float a11 = c22 * c33 - c23 * c32;
		float a21 = c23 * c31 - c21 * c33;
		float a31 = c21 * c32 - c22 * c31;

		// inverse determinant
		float invDet = 1f / (c11 * a11 + c12 * a21 + c13 * a31);

		float m11 = invDet * a11;
		float m12 = invDet * (c13 * c32 - c12 * c33);
		float m13 = invDet * (c12 * c23 - c13 * c22);

		float m21 = invDet * a21;
		float m22 = invDet * (c11 * c33 - c13 * c31);
		float m23 = invDet * (c13 * c21 - c11 * c23);

		float m31 = invDet * a31;
		float m32 = invDet * (c12 * c31 - c11 * c32);
		float m33 = invDet * (c11 * c22 - c12 * c21);

		return new Matrix3f(new float[] { m11, m12, m13, m21, m22, m23, m31, m32, m33 });
	}

	@Override
	public Matrix3f transpose() {
		float temp;

		temp = get(0, 1); set(0, 1, get(1, 0)); set(0, 1, temp); // swap m12 and m21
		temp = get(0, 2); set(0, 2, get(2, 0)); set(2, 0, temp); // swap m13 and m31
		temp = get(1, 2); set(1, 2, get(2, 1)); set(2, 1, temp); // swap m23 and m32

		return this;
	}

	@Override
	public Matrix3f transposed() {
		return new Matrix3f(new float[] {
				get(0, 0), get(1, 0), get(2, 0),
				get(0, 1), get(1, 1), get(2, 1),
				get(0, 2), get(1, 2), get(2, 2)
		});
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");

		for (int line = 0; line < 3; line++) {
			for (int i = 0; i < 3; i++) {
				sb.append(String.valueOf(components[line * 3 + i])).append("|");
			}

			sb.append("| ");
		}
		sb.append("]");

		return sb.toString();
	}

	public Matrix3f clone() {
		return new Matrix3f(Arrays.copyOf(components, components.length));
	}

	public boolean equals(Object o) {
		// generated by IntelliJ IDEA
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Matrix3f matrix3f = (Matrix3f) o;

		return Arrays.equals(components, matrix3f.components);
	}

	@Override
	public int hashCode() {
		// generated by IntelliJ IDEA
		return Arrays.hashCode(components);
	}

}
