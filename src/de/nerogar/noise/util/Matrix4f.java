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
	public Matrix4f set(float... m) {
		for (int i = 0; i < components.length; i++) {
			components[i] = m[i];
		}
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

		components = newMatrix;
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

		components = newMatrix;
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
		if (buffer == null) createBuffer();
		if (isBufferDirty) updateBuffer();

		return buffer;
	}

	private void createBuffer() {
		buffer = BufferUtils.createFloatBuffer(4 * 4);
	}

	private void updateBuffer() {
		buffer.clear();
		buffer.put(components);
		buffer.flip();

		isBufferDirty = false;
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
