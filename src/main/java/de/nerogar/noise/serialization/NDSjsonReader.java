package de.nerogar.noise.serialization;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/*package private*/ class NDSjsonReader {

	private static final String ERROR_INVALID_JSON    = "invalid json format";
	private static final String ERROR_ARRAY_OF_ARRAYS = "array of arrays is not supportet";

	// special
	private static final int TYPE_UNKNOWN       = -1;
	private static final int TYPE_OBJECT        = 0;
	// primitive
	private static final int TYPE_STRING        = 1;
	private static final int TYPE_INT           = 2;
	private static final int TYPE_FLOAT         = 3;
	private static final int TYPE_BOOLEAN       = 4;
	// array
	private static final int TYPE_ARRAY_BIT     = (1 << 16);
	private static final int TYPE_OBJECT_ARRAY  = TYPE_ARRAY_BIT + TYPE_OBJECT;
	private static final int TYPE_STRING_ARRAY  = TYPE_ARRAY_BIT + TYPE_STRING;
	private static final int TYPE_INT_ARRAY     = TYPE_ARRAY_BIT + TYPE_INT;
	private static final int TYPE_FLOAT_ARRAY   = TYPE_ARRAY_BIT + TYPE_FLOAT;
	private static final int TYPE_BOOLEAN_ARRAY = TYPE_ARRAY_BIT + TYPE_BOOLEAN;

	private PushbackReader reader;

	private int           currentChar;
	private StringBuilder sb;

	private class Value {

		public       int    type;
		public final Object object;

		public Value(int type, Object object) {
			this.type = type;
			this.object = object;
		}

		public String getString() {return (String) object;}
	}

	NDSjsonReader(Reader reader) {
		this.reader = new PushbackReader(reader);
		this.currentChar = -1;
		sb = new StringBuilder();
	}

	NDSFile read() throws IOException {
		next();

		skipWhitespace();

		if (currentChar != '{') throw new NDSjsonException(ERROR_INVALID_JSON);

		NDSFile file = new NDSFile();
		readObject(file.getData());
		return file;
	}

	private int next() throws IOException {
		currentChar = reader.read();
		return currentChar;
	}

	private void skipWhitespace() throws IOException {

		while (true) {
			int nextChar = reader.read();
			reader.unread(nextChar);

			if (currentChar == '/') {

				if (nextChar == '/') {
					next(); // skip '/' (currentChar == '/')
					next(); // skip '/' (currentChar == first char of the comment)

					while (currentChar != '\n') {
						next();
					}

					next(); // skip '\n'

				} else if (nextChar == '*') {
					next(); // skip '/' (currentChar == '*')
					next(); // skip '*' (currentChar == first char of the comment)

					int lastChar = -1;
					while (!(lastChar == '*' && currentChar == '/')) {
						lastChar = currentChar;
						next();
					}

					next(); // skip '/' (end of comment)
				}

			}

			if (!Character.isWhitespace(currentChar)) {
				break;
			} else {
				next();
			}

		}

	}

	private Value readString() throws IOException {
		sb.setLength(0);

		while (true) {
			next();
			if (currentChar == '\"') {
				next();
				break;
			}

			if (Character.isISOControl(currentChar)) throw new NDSjsonException(ERROR_INVALID_JSON);

			if (currentChar == '\\') {
				next();
				switch (currentChar) {
					case '\"': sb.append('\"'); break;
					case '\\': sb.append('\\'); break;
					case '/': sb.append('/'); break;
					case 'b': sb.append('\b'); break;
					case 'f': sb.append('\f'); break;
					case 'n': sb.append('\n'); break;
					case 'r': sb.append('\r'); break;
					case 't': sb.append('\t'); break;
					case 'u': {
						int hex0 = next();
						int hex1 = next();
						int hex2 = next();
						int hex3 = next();

						String hexString = new StringBuilder().appendCodePoint(hex0).appendCodePoint(hex1).appendCodePoint(hex2).appendCodePoint(hex3).toString();
						sb.appendCodePoint(Integer.parseInt(hexString, 16));
					} break;
					default: throw new NDSjsonException(ERROR_INVALID_JSON);
				}
			} else {
				sb.appendCodePoint(currentChar);
			}

		}

		return new Value(TYPE_STRING, sb.toString());

	}

	private Value readNumber() throws IOException {
		sb.setLength(0);
		sb.appendCodePoint(currentChar);

		boolean decimal = false;
		boolean e = false;

		while (true) {
			next();

			if ((currentChar >= '0' && currentChar <= '9') || currentChar == '-' || currentChar == '+') {
				sb.appendCodePoint(currentChar);
			} else if (currentChar == '.') {
				if (decimal) throw new NDSjsonException(ERROR_INVALID_JSON);
				if (e) throw new NDSjsonException(ERROR_INVALID_JSON);
				decimal = true;

				sb.appendCodePoint(currentChar);
			} else if (currentChar == 'e' || currentChar == 'E') {
				if (e) throw new NDSjsonException(ERROR_INVALID_JSON);
				e = true;

				sb.appendCodePoint(currentChar);
			} else {
				break;
			}
		}

		if (e || decimal) {
			return new Value(TYPE_FLOAT, sb.toString());
		} else {
			return new Value(TYPE_INT, sb.toString());
		}

	}

	private Value readBoolean() throws IOException {
		sb.setLength(0);
		sb.appendCodePoint(currentChar);

		if (currentChar == 't') {
			for (int i = 0; i < 3; i++) {
				sb.appendCodePoint(next());
			}
		} else {
			for (int i = 0; i < 4; i++) {
				sb.appendCodePoint(next());
			}
		}

		next();

		return new Value(TYPE_BOOLEAN, sb.toString());

	}

	private Value readNull() throws IOException {
		sb.setLength(0);
		sb.appendCodePoint(currentChar);

		for (int i = 0; i < 3; i++) {
			sb.appendCodePoint(next());
		}

		if (!sb.toString().equals("null")) throw new NDSjsonException(ERROR_INVALID_JSON);

		next();

		return new Value(TYPE_UNKNOWN, null);

	}

	private Value readArray() throws IOException {

		List<Value> list = new ArrayList<>();
		int type = TYPE_UNKNOWN;

		next(); // skip '['

		while (true) {
			skipWhitespace();
			if (currentChar == ']') {
				next();
				break;
			}

			Value nextValue = readValue();

			list.add(nextValue);

			if (type == TYPE_UNKNOWN) {
				type = nextValue.type;
			} else if (nextValue.type != TYPE_UNKNOWN && type != nextValue.type) {
				throw new NDSjsonException(ERROR_INVALID_JSON);
			}

			skipWhitespace();

			if (currentChar == ',') {
				next();
			}
		}

		if (type == TYPE_UNKNOWN) type = TYPE_OBJECT;

		if ((type & TYPE_ARRAY_BIT) != 0) throw new NDSjsonException(ERROR_ARRAY_OF_ARRAYS);

		if (type == TYPE_OBJECT) {
			NDSNodeObject[] array = new NDSNodeObject[list.size()];
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).object == null) {
					array[i] = new NDSNodeObject(null, true);
				} else {
					array[i] = (NDSNodeObject) list.get(i).object;
				}
			}
			return new Value(TYPE_OBJECT_ARRAY, array);
		} else if (type == TYPE_STRING) {
			String[] array = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				array[i] = list.get(i).getString();
			}
			return new Value(TYPE_STRING_ARRAY, array);
		} else if (type == TYPE_INT) {
			int[] array = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				try {
					array[i] = Integer.parseInt(list.get(i).getString());
				} catch (NumberFormatException e) {
					array[i] = -1;
				}
			}
			return new Value(TYPE_INT_ARRAY, array);
		} else if (type == TYPE_FLOAT) {
			float[] array = new float[list.size()];
			for (int i = 0; i < list.size(); i++) {
				array[i] = Float.parseFloat(list.get(i).getString());
			}
			return new Value(TYPE_FLOAT_ARRAY, array);
		} else if (type == TYPE_BOOLEAN) {
			boolean[] array = new boolean[list.size()];
			for (int i = 0; i < list.size(); i++) {
				array[i] = Boolean.parseBoolean(list.get(i).getString());
			}
			return new Value(TYPE_BOOLEAN_ARRAY, array);
		} else {
			throw new NDSjsonException(ERROR_INVALID_JSON);
		}

	}

	private Value readValue() throws IOException {
		switch (currentChar) {
			case '\"':
				return readString();
			case '-':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return readNumber();
			case '{':
				return readObject(new NDSNodeObject(null));
			case '[':
				return readArray();
			case 't':
			case 'f':
				return readBoolean();
			case 'n':
				return readNull();
			default:
				throw new NDSjsonException(ERROR_INVALID_JSON);
		}
	}

	private Value readObject(NDSNodeObject object) throws IOException {

		Value value = new Value(TYPE_OBJECT, object);

		next(); // throw away the '{' character

		while (true) {
			skipWhitespace();
			if (currentChar == '}') {
				next();
				return value;
			}

			if (currentChar != '\"') throw new NDSjsonException(ERROR_INVALID_JSON);
			readString();
			String childName = sb.toString();

			skipWhitespace();

			if (currentChar != ':') throw new NDSjsonException(ERROR_INVALID_JSON);
			next();

			skipWhitespace();
			Value childValue = readValue();

			if (childValue.type == TYPE_UNKNOWN) childValue.type = TYPE_OBJECT;

			switch (childValue.type) {
				case TYPE_OBJECT:
					if (childValue.object == null) {
						object.addObject(new NDSNodeObject(childName, true));
					} else {
						NDSNodeObject childObject = (NDSNodeObject) childValue.object;
						childObject.name = childName;
						object.addObject(childObject);
					}
					break;
				case TYPE_STRING:
					object.addStringUTF8(childName, childValue.getString());
					break;
				case TYPE_INT:
					try {
						object.addInt(childName, Integer.parseInt(childValue.getString()));
					} catch (NumberFormatException e) {
						object.addInt(childName, -1);
					}
					break;
				case TYPE_FLOAT:
					object.addFloat(childName, Float.parseFloat(childValue.getString()));
					break;
				case TYPE_BOOLEAN:
					object.addBoolean(childName, Boolean.parseBoolean(childValue.getString()));
					break;
				case TYPE_OBJECT_ARRAY:
					object.addObjectArray(childName, (NDSNodeObject[]) childValue.object);
					break;
				case TYPE_STRING_ARRAY:
					object.addStringUTF8Array(childName, (String[]) childValue.object);
					break;
				case TYPE_INT_ARRAY:
					object.addIntArray(childName, (int[]) childValue.object);
					break;
				case TYPE_FLOAT_ARRAY:
					object.addFloatArray(childName, (float[]) childValue.object);
					break;
				case TYPE_BOOLEAN_ARRAY:
					object.addBooleanArray(childName, (boolean[]) childValue.object);
					break;
			}

			skipWhitespace();

			if (currentChar == ',') next();
		}

	}

}
