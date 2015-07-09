package de.nerogar.noise.util;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

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
	public float get(int collumnIndex, int lineIndex) {
		return components[lineIndex * 4 + collumnIndex];
	}

	@Override
	public Matrix4f set(int collumnIndex, int lineIndex, float f) {
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
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				set(i, j, m.get(i, j));
			}
		}
		isBufferDirty = true;

		return this;
	}

	@Override
	public Matrix4f set(float[] m) {
		for (int i = 0; i < components.length; i++) {
			components[i] = m[i];
		}
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
				set(i, j, get(i, j) + m.get(i, j));
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
				set(i, j, get(i, j) - m.get(i, j));
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

			float c0 = get(0, j);
			float c1 = get(1, j);
			float c2 = get(2, j);
			float c3 = get(3, j);

			for (int i = 0; i < 4; i++) {
				float sum = 0;

				sum += m.get(i, 0) * c0;
				sum += m.get(i, 1) * c1;
				sum += m.get(i, 2) * c2;
				sum += m.get(i, 3) * c3;

				set(i, j, sum);
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
					sum += m.get(i, w) * get(w, j);
				}

				newMatrix[j * 4 + i] = sum;
			}
		}

		return new Matrix4f(newMatrix);
	}

	@Override
	public Matrix4f multiplyLeft(Matrix4f m) {
		for (int i = 0; i < 4; i++) {

			float c0 = get(i, 0);
			float c1 = get(i, 1);
			float c2 = get(i, 2);
			float c3 = get(i, 3);

			for (int j = 0; j < 4; j++) {
				float sum = 0;

				sum += c0 * m.get(0, j);
				sum += c1 * m.get(1, j);
				sum += c2 * m.get(2, j);
				sum += c3 * m.get(3, j);

				set(i, j, sum);
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
					sum += get(i, w) * m.get(w, j);
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
	public Matrix4f transpose() {
		for (int i = 0; i < 4; i++) {
			for (int j = i + 1; j < 4; j++) {
				float temp = get(i, j);
				set(i, j, get(j, i));
				set(j, i, temp);
			}
		}
		return this;
	}

	@Override
	public Matrix4f transposed() {
		return new Matrix4f(new float[]{
				get(0, 0), get(0, 1), get(0, 2), get(0, 3),
				get(1, 0), get(1, 1), get(1, 2), get(1, 3),
				get(2, 0), get(2, 1), get(2, 2), get(2, 3),
				get(3, 0), get(3, 1), get(3, 2), get(3, 3)
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

}
