package de.nerogar.noise.serialization;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class NDSNodeRoot extends NDSNodeObject {

	private long           currentDataPointer;
	private Queue<NDSNode> rawNodes;

	public NDSNodeRoot(String name) {
		super(name);

		currentDataPointer = 0;
		rawNodes = new ArrayDeque<>();
	}

	/**
	 * add a node to the raw node list and reserve enough space
	 *
	 * @param node the node
	 * @return the pointer to the reserved location
	 */
	protected long reserveRawNode(NDSNode node) {
		long current = currentDataPointer;
		rawNodes.add(node);
		currentDataPointer += node.getContentSizeInBytes();

		return current;
	}

	/**
	 * add a node to the raw node list
	 *
	 * @param node the node
	 */
	protected void addRawNode(NDSNode node) {
		rawNodes.add(node);
	}

	protected void resetDataPointer() {
		currentDataPointer = 0;
	}

	protected void resetRawNodes() {
		rawNodes.clear();
	}

	protected void writeTree(NDSDataOutputStream out) throws IOException {
		writeTree(out, this, true);
	}

	protected void writeRaw(NDSDataOutputStream out) throws IOException {
		out.resetByteIndex();

		while (!rawNodes.isEmpty()) {
			NDSNode node = rawNodes.remove();
			node.writeContent(out, this);
		}
	}

	protected void readTree(NDSDataInputStream in) throws IOException {
		readTree(in, this, true);
	}

	protected void readRaw(NDSDataInputStream in) throws IOException {
		in.resetByteIndex();

		while (!rawNodes.isEmpty()) {
			NDSNode rawNode = rawNodes.remove();
			in.skipToByteIndex(rawNode.pointer);
			rawNode.readContent(in, this);
		}
	}

}
