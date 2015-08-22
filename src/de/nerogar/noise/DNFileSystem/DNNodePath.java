package de.nerogar.noise.DNFileSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DNNodePath {
	protected String name;
	protected HashMap<String, DNNodePath> paths = new HashMap<String, DNNodePath>();
	protected HashMap<String, DNNode> nodes = new HashMap<String, DNNode>();

	public DNNodePath(String name) {
		this.name = name;
	}

	/**
	 * Returns a HashMap containing all paths.
	 */
	public HashMap<String, DNNodePath> getPaths() {
		return paths;
	}

	/**
	 * Returns a HashMap containing all nodes.
	 */
	public HashMap<String, DNNode> getNodes() {
		return nodes;
	}

	/**
	 * Returns the DNNodePath object specified by the parameter <b>pathName</b>.<br>
	 * If the specified path does not exist, it gets created.
	 * 
	 * @param pathName the name of the path
	 */
	public DNNodePath getPath(String pathName) {
		String[] pathArray = pathName.split("\\.");
		DNNodePath localPath = this;
		if (!pathName.equals("")) {
			for (int i = 0; i < pathArray.length; i++) {
				DNNodePath tempPath = localPath.getPaths().get(pathArray[i]);
				if (tempPath == null) {
					tempPath = new DNNodePath(pathArray[i]);
					localPath.paths.put(pathArray[i], tempPath);
				}
				localPath = tempPath;
			}
		}

		return localPath;
	}

	/**
	 * Adds a DNNodePath object to the current path.<br>
	 * The name has to be set in the obect itself.
	 * 
	 * @param path the DNNodePath object to add
	 */
	public void addPathObject(DNNodePath path) {
		paths.put(path.name, path);
	}

	/**
	 * Returns the DNNode object specified by the parameter <b>name</b>.<br>
	 * The name can either be the name of the node or a path to the node.
	 * 
	 * @param name can either be the name of the node or a path to the node.<br>
	 */
	public DNNode getNode(String name) {

		return getPath(getPathName(name)).getNodes().get((getNodeName(name)));
	}

	/**
	 * Adds a new integer node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addInt(String name, int... value) {
		if (value == null) {
			addNode(name, DNHelper.INTEGER, -1, value);
		} else {
			addNode(name, DNHelper.INTEGER, value.length, value);
		}
	}

	/**
	 * Adds a new long node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addLong(String name, long... value) {
		if (value == null) {
			addNode(name, DNHelper.LONG, -1, value);
		} else {
			addNode(name, DNHelper.LONG, value.length, value);
		}
	}

	/**
	 * Adds a new byte node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addByte(String name, byte... value) {
		if (value == null) {
			addNode(name, DNHelper.BYTE, -1, value);
		} else {
			addNode(name, DNHelper.BYTE, value.length, value);
		}
	}

	/**
	 * Adds a new char node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addChar(String name, char... value) {
		if (value == null) {
			addNode(name, DNHelper.CHAR, -1, value);
		} else {
			addNode(name, DNHelper.CHAR, value.length, value);
		}
	}

	/**
	 * Adds a new string node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addString(String name, String... value) {
		if (value == null) {
			addNode(name, DNHelper.STRING, -1, value);
		} else {
			addNode(name, DNHelper.STRING, value.length, value);
		}
	}

	/**
	 * Adds a new float node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addFloat(String name, float... value) {
		if (value == null) {
			addNode(name, DNHelper.FLOAT, -1, value);
		} else {
			addNode(name, DNHelper.FLOAT, value.length, value);
		}
	}

	/**
	 * Adds a new double node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addDouble(String name, double... value) {
		if (value == null) {
			addNode(name, DNHelper.DOUBLE, -1, value);
		} else {
			addNode(name, DNHelper.DOUBLE, value.length, value);
		}
	}

	/**
	 * Adds a new boolean node to the system. The path is specified by the parameter <b>name</b>.
	 * @param name  can either be the name of the node or a path to the node.
	 * @param value the value that gets stored in the new node.
	 */
	public void addBoolean(String name, boolean... value) {
		if (value == null) {
			addNode(name, DNHelper.BOOLEAN, -1, value);
		} else {
			addNode(name, DNHelper.BOOLEAN, value.length, value);
		}
	}

	/**
	 * Returns the integer stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public int getInt(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.INTEGER) return ((int[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the integer array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public int[] getIntArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.INTEGER) {
				return (int[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the long stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public long getLong(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.LONG) return ((long[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the long array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public long[] getLongArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.LONG) {
				return (long[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the byte stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public byte getByte(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.BYTE) return ((byte[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the byte array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public byte[] getByteArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.BYTE) {
				return (byte[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the char stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public char getChar(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.CHAR) return ((char[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the byte array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public char[] getCharArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.CHAR) {
				return (char[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the string stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public String getString(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.STRING) return ((String[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the string array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public String[] getStringArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.STRING) {
				return (String[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the float stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public float getFloat(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.FLOAT) return ((float[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the float array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public float[] getFloatArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.FLOAT) {
				return (float[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the double stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public double getDouble(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.DOUBLE) return ((double[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the double array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public double[] getDoubleArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.DOUBLE) {
				return (double[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	/**
	 * Returns the boolean stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public boolean getBoolean(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length == 1) {
			if (node.typ == DNHelper.BOOLEAN) return ((boolean[]) node.value)[0];
		}

		throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
	}

	/**
	 * Returns the boolean array stored in the node specified by the parameter <b>name</b>.
	 * @param name the name of the node
	 * @throws NoSuchNodeException if the node does not exist or is another type
	 */
	public boolean[] getBooleanArray(String name) {
		DNNode node = getNode(name);
		if (node == null) throw new NoSuchNodeException(NoSuchNodeException.NODE_NOT_FOUND);
		if (node.length >= 0) {
			if (node.typ == DNHelper.BOOLEAN) {
				return (boolean[]) node.value;
			} else {
				throw new NoSuchNodeException(NoSuchNodeException.WRONG_NODE_TYPE);
			}
		}

		return null;
	}

	protected void addNode(String newName, byte typ, int length, Object value) {
		DNNodePath localPath = getPath(getPathName(newName));
		String newNodeName = getNodeName(newName);
		localPath.nodes.put(newNodeName, new DNNode(newNodeName, typ, length, value));
	}

	protected String getPathName(String pathName) {
		ArrayList<String> tempPath = new ArrayList<String>(Arrays.asList(pathName.split("\\.")));
		tempPath.remove(tempPath.size() - 1);
		StringBuilder newPath = new StringBuilder();
		for (int i = 0; i < tempPath.size(); i++) {
			newPath.append(i < tempPath.size() - 1 ? tempPath.get(i) + "." : tempPath.get(i));
		}
		return newPath.toString();
	}

	protected String getNodeName(String pathName) {
		String[] pathNameArray = pathName.split("\\.");
		return pathNameArray[pathNameArray.length - 1];
	}

	protected int calcSize() {
		int size = 0;
		for (DNNode node : nodes.values()) {
			size += node.calcSize();
		}

		for (DNNodePath node : paths.values()) {
			size += DNHelper.FOLDERSIZE;
			size += node.name.length() * 2;
			size += node.calcSize();
		}

		return size;
	}

}
