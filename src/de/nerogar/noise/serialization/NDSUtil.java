package de.nerogar.noise.serialization;

import java.util.Arrays;

import static de.nerogar.noise.serialization.NDSConstants.ERROR_NOT_AN_ARRAY;
import static de.nerogar.noise.serialization.NDSConstants.LENGTH_MASK;

/*package private*/ class NDSUtil {

	/**
	 * calculates the length in bits of a data type that supports it
	 *
	 * @param type the data type
	 * @return the length in bits
	 */
	protected static int getLengthInBits(int type) { return 1 << (type & LENGTH_MASK); }

	protected static String arrayToString(Object array) {
		if(array == null) {
			return "null";
		}else if (array instanceof byte[]) {
			return Arrays.toString((byte[]) array);
		} else if (array instanceof short[]) {
			return Arrays.toString((short[]) array);
		} else if (array instanceof int[]) {
			return Arrays.toString((int[]) array);
		} else if (array instanceof long[]) {
			return Arrays.toString((long[]) array);
		} else if (array instanceof float[]) {
			return Arrays.toString((float[]) array);
		} else if (array instanceof double[]) {
			return Arrays.toString((double[]) array);
		} else if (array instanceof boolean[]) {
			return Arrays.toString((boolean[]) array);
		} else if (array instanceof char[]) {
			return Arrays.toString((char[]) array);
		} else if (array instanceof Object[]) {
			return Arrays.deepToString((Object[]) array);
		}

		throw new NDSException(ERROR_NOT_AN_ARRAY);
	}

}
