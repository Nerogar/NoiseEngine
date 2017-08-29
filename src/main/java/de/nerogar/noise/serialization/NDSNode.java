package de.nerogar.noise.serialization;

import java.io.IOException;

import static de.nerogar.noise.serialization.NDSConstants.ERROR_ONLY_ASCII;

public abstract class NDSNode {

	protected int    type;
	protected String name;

	/** pointer to raw data, used while writing and reading files */
	protected long pointer;

	public NDSNode(String name) {
		setName(name);
	}

	public void setName(String name) {
		if (name == null) {
			this.name = null;
			return;
		}

		if (!NDSFile.checkAscii(name)) {
			throw new IllegalArgumentException(ERROR_ONLY_ASCII);
		}

		this.name = name;
	}

	public String getName() {
		return name;
	}

	// --- [ write

	protected abstract void writeContent(NDSDataOutputStream out, NDSNodeRoot root) throws IOException;

	protected final void writeTree(NDSDataOutputStream out, NDSNodeRoot root, boolean writeHeader) throws IOException {
		if (writeHeader) {
			out.writeUnsignedByte((short) type);
			out.writeASCIIString(name, true);
		}

		if (isRawNode()) {
			if (isNull()) {
				out.writeLong(-1);
			} else {
				pointer = root.reserveRawNode(this);
				out.writeLong(pointer);
			}
		} else {
			writeContent(out, root);
		}
	}

	// --- [ read

	protected abstract void readContent(NDSDataInputStream in, NDSNodeRoot root) throws IOException;

	/**
	 * reads the subtree of this node
	 *
	 * @param in         the input stream
	 * @param root       the root node of this tree
	 * @param readHeader true if the header (type and name) should be read
	 * @throws IOException
	 */
	protected final void readTree(NDSDataInputStream in, NDSNodeRoot root, boolean readHeader) throws IOException {
		if (readHeader) {
			type = in.readUnsignedByte();
			name = in.readASCIIString();
		}

		if (isRawNode()) {
			pointer = in.readLong();

			if (pointer >= 0) {
				root.addRawNode(this);
			}
		} else {
			readContent(in, root);
		}
	}

	// --- [ util

	/**
	 * @return true, if the value saved in this node is null
	 */
	public abstract boolean isNull();

	/**
	 * @return true, if the node content is written in the raw data
	 */
	protected abstract boolean isRawNode();

	/**
	 * @return true, if the subtree under this node has a node that writes to the raw data
	 */
	protected abstract boolean hasRawNode();

	/**
	 * @return the size of the content in bytes
	 */
	protected abstract long getContentSizeInBytes();

}
