package de.nerogar.noise.serialization;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

/*package private*/ class NDSjsonWriter {

	private static final String ERROR_UNSUPPORTED_TYPE = "unsupported type";

	private BufferedWriter writer;

	NDSjsonWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	void write(NDSFile ndsFile) throws IOException {
		writeObject(0, ndsFile.getData());
	}

	private void indent(int level) throws IOException {
		for (int i = 0; i < level; i++) {
			writer.write('\t');
		}
	}

	private String escapeString(String s) {
		StringBuilder sb = new StringBuilder();

		s.codePoints().forEach((codepoint) -> {

			switch (codepoint) {
				case '\\': sb.append("\\\\"); break;
				case '\"': sb.append("\\\""); break;
				case '\b': sb.append("\\b"); break;
				case '\f': sb.append("\\f"); break;
				case '\n': sb.append("\\n"); break;
				case '\r': sb.append("\\r"); break;
				case '\t': sb.append("\\t"); break;
				default: sb.appendCodePoint(codepoint); break;
			}

		});

		return sb.toString();
	}

	private void writeObject(int indentLevel, NDSNodeObject object) throws IOException {

		if (object.isNull()) {
			writer.write("null");
		} else {
			writer.write("{\n");

			Collection<NDSNode> children = object.getChildren();
			int i = 0;
			for (NDSNode child : children) {

				indent(indentLevel + 1);
				writer.write("\"");
				writer.write(escapeString(child.name));
				writer.write("\": ");

				if (child instanceof NDSNodeValue) {
					NDSNodeValue value = (NDSNodeValue) child;

					if (value.isArray()) {
						writeArray(indentLevel + 1, value);
					} else {
						switch (value.type) {
							case NDSConstants.SINGLE_VALUE | NDSConstants.INTEGER_SIGNED | NDSConstants.LENGTH_32:
								writer.write(String.valueOf(value.getInt()));
								break;
							case NDSConstants.SINGLE_VALUE | NDSConstants.TYPE_FLOAT | NDSConstants.LENGTH_32:
								writer.write(String.valueOf(value.getFloat()));
								break;
							case NDSConstants.SINGLE_VALUE | NDSConstants.STRING_UTF8:
								writer.write("\"");
								writer.write(escapeString(value.getStringUTF8()));
								writer.write("\"");
								break;
							case NDSConstants.SINGLE_VALUE | NDSConstants.BOOLEAN:
								writer.write(String.valueOf(value.getBoolean()));
								break;
							default:
								throw new NDSjsonException(ERROR_UNSUPPORTED_TYPE);
						}
					}
				} else if (child instanceof NDSNodeObject) {
					writeObject(indentLevel + 1, (NDSNodeObject) child);
				}

				if (++i < children.size()) writer.write(",\n");

			}

			writer.write("\n");
			indent(indentLevel);
			writer.write("}");
		}

	}

	private void writeIntArray(int indentLevel, int[] array) throws IOException {
		indent(indentLevel + 1);
		for (int i = 0; i < array.length; i++) {
			writer.write(String.valueOf(array[i]));

			if (i < array.length - 1) writer.write(",");
		}
		writer.write("\n");
	}

	private void writeFloatArray(int indentLevel, float[] array) throws IOException {
		indent(indentLevel + 1);
		for (int i = 0; i < array.length; i++) {
			writer.write(String.valueOf(array[i]));

			if (i < array.length - 1) writer.write(",");
		}
		writer.write("\n");
	}

	private void writeBooleanArray(int indentLevel, boolean[] array) throws IOException {
		indent(indentLevel + 1);
		for (int i = 0; i < array.length; i++) {
			writer.write(String.valueOf(array[i]));

			if (i < array.length - 1) writer.write(",");
		}
		writer.write("\n");
	}

	private void writeStringArray(int indentLevel, String[] array) throws IOException {
		for (int i = 0; i < array.length; i++) {
			indent(indentLevel + 1);

			writer.write("\"");
			writer.write(escapeString(String.valueOf(array[i])));
			writer.write("\"");

			if (i < array.length - 1) writer.write(",");

			writer.write("\n");
		}
	}

	private void writeObjectArray(int indentLevel, NDSNodeObject[] array) throws IOException {
		for (int i = 0; i < array.length; i++) {
			indent(indentLevel + 1);
			writeObject(indentLevel + 1, array[i]);

			if (i < array.length - 1) writer.write(",");

			writer.write("\n");
		}
	}

	private void writeArray(int indentLevel, NDSNodeValue array) throws IOException {
		if (array.isNull()) {
			writer.write("null");
		} else {

			writer.write("[\n");

			switch (array.type) {
				case NDSConstants.ARRAY_VALUE | NDSConstants.INTEGER_SIGNED | NDSConstants.LENGTH_32:
					writeIntArray(indentLevel, array.getIntArray());
					break;
				case NDSConstants.ARRAY_VALUE | NDSConstants.TYPE_FLOAT | NDSConstants.LENGTH_32:
					writeFloatArray(indentLevel, array.getFloatArray());
					break;
				case NDSConstants.ARRAY_VALUE | NDSConstants.STRING_UTF8:
					writeStringArray(indentLevel, array.getStringUTF8Array());
					break;
				case NDSConstants.ARRAY_VALUE | NDSConstants.BOOLEAN:
					writeBooleanArray(indentLevel, array.getBooleanArray());
					break;
				case NDSConstants.ARRAY_VALUE | NDSConstants.OBJECT:
					writeObjectArray(indentLevel, array.getObjectArray());
					break;
				default:
					throw new NDSjsonException(ERROR_UNSUPPORTED_TYPE);
			}

			indent(indentLevel);
			writer.write("]");
		}
	}

}
