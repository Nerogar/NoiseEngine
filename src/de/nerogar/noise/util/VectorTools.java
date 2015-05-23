package de.nerogar.noise.util;

public class VectorTools {

	public static int getGreatestComponentIndex(Vectorf<?> v) {
		int greatestIndex = 0;

		for (int i = 0; i < v.getComponentCount(); i++) {
			if (v.get(i) > v.get(greatestIndex)) greatestIndex = i;
		}

		return greatestIndex;
	}

	public static int getSmallestComponentIndex(Vectorf<?> v) {
		int smallestIndex = 0;

		for (int i = 0; i < v.getComponentCount(); i++) {
			if (v.get(i) < v.get(smallestIndex)) smallestIndex = i;
		}

		return smallestIndex;
	}
}
