package de.nerogar.noise.util;

import java.util.Arrays;

public class MatrixNf implements Matrixf<MatrixNf> {

	private float[] components;
	private int componentCount;

	public MatrixNf(int componentCount) {
		this.componentCount = componentCount;
		components = new float[componentCount * componentCount];
	}

	private MatrixNf(float[] components, int componentCount) {
		this.components = components;
		this.componentCount = componentCount;
	}

	@Override
	public int getComponentCount() {
		return componentCount;
	}

	@Override
	public MatrixNf newInstance() {
		return new MatrixNf(componentCount);
	}

	@Override
	public float get(int lineIndex, int collumnIndex) {
		return components[lineIndex * componentCount + collumnIndex];
	}

	@Override
	public MatrixNf set(int lineIndex, int collumnIndex, float f) {
		components[lineIndex * componentCount + collumnIndex] = f;
		return this;
	}

	@Override
	public MatrixNf set(float allComponents) {
		for (int i = 0; i < components.length; i++) {
			components[i] = allComponents;
		}

		return this;
	}

	@Override
	public MatrixNf set(MatrixNf m) {
		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				set(j, i, m.get(j, i));
			}
		}

		return this;
	}

	@Override
	public MatrixNf set(float[] m) {
		for (int i = 0; i < components.length; i++) {
			components[i] = m[i];
		}

		return this;
	}

	@Override
	public MatrixNf add(MatrixNf m) {
		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				set(j, i, get(j, i) + m.get(j, i));
			}
		}

		return this;
	}

	@Override
	public MatrixNf added(MatrixNf m) {
		return clone().add(m);
	}

	@Override
	public MatrixNf subtract(MatrixNf m) {
		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				set(j, i, get(j, i) - m.get(j, i));
			}
		}

		return this;
	}

	@Override
	public MatrixNf subtracted(MatrixNf m) {
		return clone().subtract(m);
	}

	@Override
	public MatrixNf multiply(float f) {
		for (int i = 0; i < components.length; i++) {
			components[i] *= f;
		}

		return this;
	}

	@Override
	public MatrixNf multiplied(float f) {
		float[] newMatrix = new float[componentCount * componentCount];
		for (int i = 0; i < components.length; i++) {
			newMatrix[i] = components[i] * f;
		}

		return new MatrixNf(newMatrix, componentCount);
	}

	@Override
	public MatrixNf multiplyRight(MatrixNf m) {
		float[] newMatrix = new float[componentCount * componentCount];

		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				float sum = 0;

				for (int w = 0; w < componentCount; w++) {
					sum += m.get(w, i) * get(j, w);
				}

				newMatrix[j * componentCount + i] = sum;
			}
		}

		components = newMatrix;

		return this;
	}

	@Override
	public MatrixNf multipliedRight(MatrixNf m) {
		float[] newMatrix = new float[componentCount * componentCount];

		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				float sum = 0;

				for (int w = 0; w < componentCount; w++) {
					sum += m.get(w, i) * get(j, w);
				}

				newMatrix[j * componentCount + i] = sum;
			}
		}

		return new MatrixNf(newMatrix, componentCount);
	}

	@Override
	public MatrixNf multiplyLeft(MatrixNf m) {
		float[] newMatrix = new float[componentCount * componentCount];

		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				float sum = 0;

				for (int w = 0; w < componentCount; w++) {
					sum += get(w, i) * m.get(j, w);
				}

				newMatrix[j * componentCount + i] = sum;
			}
		}

		components = newMatrix;

		return this;
	}

	@Override
	public MatrixNf multipliedLeft(MatrixNf m) {
		float[] newMatrix = new float[componentCount * componentCount];

		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				float sum = 0;

				for (int w = 0; w < componentCount; w++) {
					sum += get(w, i) * m.get(j, w);
				}

				newMatrix[j * componentCount + i] = sum;
			}
		}

		return new MatrixNf(newMatrix, componentCount);
	}

	@Override
	public MatrixNf invert() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MatrixNf inverted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MatrixNf transpose() {
		for (int i = 0; i < componentCount; i++) {
			for (int j = i + 1; j < componentCount; j++) {
				float temp = get(j, i);
				set(j, i, get(i, j));
				set(i, j, temp);
			}
		}
		return null;
	}

	@Override
	public MatrixNf transposed() {
		float[] newMatrix = new float[components.length];

		for (int i = 0; i < componentCount; i++) {
			for (int j = 0; j < componentCount; j++) {
				newMatrix[i + j * componentCount] = get(i, j);
			}
		}
		return new MatrixNf(newMatrix, componentCount);
	}

	@Override
	public MatrixNf clone() {
		return new MatrixNf(Arrays.copyOf(components, components.length), componentCount);
	}

	@Override
	public boolean equals(Object o) {
		// generated by IntelliJ IDEA
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MatrixNf matrixNf = (MatrixNf) o;

		return Arrays.equals(components, matrixNf.components);
	}

	@Override
	public int hashCode() {
		// generated by IntelliJ IDEA
		return Arrays.hashCode(components);
	}
}
