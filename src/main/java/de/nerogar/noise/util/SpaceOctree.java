package de.nerogar.noise.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

public class SpaceOctree<T> implements Set<T> {

	private final int   MAX_LEAF_ELEMENTS;
	private final float MIN_NODE_SIZE;

	private class Node {

		// in the format childXYZ, N for negative, P for positive
		private Node childNNN;
		private Node childNNP;
		private Node childNPN;
		private Node childNPP;
		private Node childPNN;
		private Node childPNP;
		private Node childPPN;
		private Node childPPP;

		private Node parent;

		private boolean isLeaf;

		private int   depth;
		private float size;
		private float halfSize;
		private float minX, minY, minZ;

		private Set<T> elements = new HashSet<>();

		public Node(int depth, float size, float minX, float minY, float minZ) {
			this.depth = depth;

			this.size = size;
			this.halfSize = this.size / 2;

			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;

			isLeaf = true;
		}

		public Node(Node parent, float size, float minX, float minY, float minZ) {
			this.depth = parent.depth + 1;
			this.parent = parent;

			this.size = size;
			this.halfSize = this.size / 2;

			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;

			this.isLeaf = true;
		}

		public Node(int depth, float size, float minX, float minY, float minZ,
				Node childNNN, Node childNNP, Node childNPN, Node childNPP, Node childPNN, Node childPNP, Node childPPN, Node childPPP) {

			this.depth = depth;

			this.size = size;
			this.halfSize = this.size / 2;

			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;

			this.childNNN = childNNN;
			this.childNNP = childNNP;
			this.childNPN = childNPN;
			this.childNPP = childNPP;
			this.childPNN = childPNN;
			this.childPNP = childPNP;
			this.childPPN = childPPN;
			this.childPPP = childPPP;

			this.childNNN.parent = this;
			this.childNNP.parent = this;
			this.childNPN.parent = this;
			this.childNPP.parent = this;
			this.childPNN.parent = this;
			this.childPNP.parent = this;
			this.childPPN.parent = this;
			this.childPPP.parent = this;

			this.isLeaf = false;
		}

		public boolean add(T element, Bounding bounding) {
			Vector3f point = bounding.point();
			Node child = getChildAt(point.getX(), point.getY(), point.getZ());

			if (!bounding.isInside(minX, minY, minZ, minX + size, minY + size, minZ + size)) {
				if (parent == null) {
					createRootAt(point.getX(), point.getY(), point.getZ());
				}
				return parent.add(element, bounding);
			} else if (!isLeaf && bounding.isInside(child.minX, child.minY, child.minZ, child.minX + halfSize, child.minY + halfSize, child.minZ + halfSize)) {
				return child.add(element, bounding);
			} else {
				ArrayList<Node> containingNodes = new ArrayList<>();
				add(element, bounding, containingNodes, true, true, true, false, false, false, false, false, false);
				containingNodes.trimToSize();
				addLookup(element, containingNodes);

				for (Node containingNode : containingNodes) {
					if (containingNode.isLeaf && containingNode.elements.size() >= MAX_LEAF_ELEMENTS && containingNode.size >= MIN_NODE_SIZE) {
						containingNode.split();
					}
				}
			}

			return true;

		}

		private void add(T element, Bounding bounding, List<Node> containingNodes, boolean allowSplitX, boolean allowSplitY, boolean allowSplitZ,
				boolean allowOverlapXP, boolean allowOverlapXN, boolean allowOverlapYP, boolean allowOverlapYN, boolean allowOverlapZP, boolean allowOverlapZN) {

			boolean splitX = bounding.intersectsXPlane(minX + halfSize);
			boolean splitY = bounding.intersectsYPlane(minY + halfSize);
			boolean splitZ = bounding.intersectsZPlane(minZ + halfSize);

			boolean split = splitX || splitY || splitZ;

			// if one of the split directions is not allowed, don't split
			if ((splitX && !allowSplitX) || (splitY && !allowSplitY) || (splitZ && !allowSplitZ)) split = false;

			if (!isLeaf && split) {

				if (bounding.overlaps(minX, minY, minZ, minX + halfSize, minY + halfSize, minZ + halfSize)) {
					childNNN.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP || splitX, allowOverlapXN, allowOverlapYP || splitY, allowOverlapYN, allowOverlapZP || splitZ, allowOverlapZN
					            );
				}
				if (bounding.overlaps(minX, minY, minZ + halfSize, minX + halfSize, minY + halfSize, minZ + size)) {
					childNNP.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP || splitX, allowOverlapXN, allowOverlapYP || splitY, allowOverlapYN, allowOverlapZP, allowOverlapZN || splitZ
					            );
				}
				if (bounding.overlaps(minX, minY + halfSize, minZ, minX + halfSize, minY + size, minZ + halfSize)) {
					childNPN.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP || splitX, allowOverlapXN, allowOverlapYP, allowOverlapYN || splitY, allowOverlapZP || splitZ, allowOverlapZN
					            );
				}
				if (bounding.overlaps(minX, minY + halfSize, minZ + halfSize, minX + halfSize, minY + size, minZ + size)) {
					childNPP.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP || splitX, allowOverlapXN, allowOverlapYP, allowOverlapYN || splitY, allowOverlapZP, allowOverlapZN || splitZ
					            );
				}
				if (bounding.overlaps(minX + halfSize, minY, minZ, minX + size, minY + halfSize, minZ + halfSize)) {
					childPNN.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP, allowOverlapXN || splitX, allowOverlapYP || splitY, allowOverlapYN, allowOverlapZP || splitZ, allowOverlapZN
					            );
				}
				if (bounding.overlaps(minX + halfSize, minY, minZ + halfSize, minX + size, minY + halfSize, minZ + size)) {
					childPNP.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP, allowOverlapXN || splitX, allowOverlapYP || splitY, allowOverlapYN, allowOverlapZP, allowOverlapZN || splitZ
					            );
				}
				if (bounding.overlaps(minX + halfSize, minY + halfSize, minZ, minX + size, minY + size, minZ + halfSize)) {
					childPPN.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP, allowOverlapXN || splitX, allowOverlapYP, allowOverlapYN || splitY, allowOverlapZP || splitZ, allowOverlapZN
					            );
				}
				if (bounding.overlaps(minX + halfSize, minY + halfSize, minZ + halfSize, minX + size, minY + size, minZ + size)) {
					childPPP.add(element, bounding, containingNodes, allowSplitX && !splitX, allowSplitY && !splitY, allowSplitZ && !splitZ,
					             allowOverlapXP, allowOverlapXN || splitX, allowOverlapYP, allowOverlapYN || splitY, allowOverlapZP, allowOverlapZN || splitZ
					            );
				}

			} else {

				if (isLeaf) {
					elements.add(element);
					containingNodes.add(this);
				} else {
					Vector3f point = bounding.point();
					Node child = getChildAt(point.getX(), point.getY(), point.getZ());

					//if (allowOverlapXN && allowOverlapYN && allowOverlapZN) child = childNNN;
					//else if (allowOverlapXN && allowOverlapYN && allowOverlapZP) child = childNNP;
					//else if (allowOverlapXN && allowOverlapYP && allowOverlapZN) child = childNPN;
					//else if (allowOverlapXN && allowOverlapYP && allowOverlapZP) child = childNPP;
					//else if (allowOverlapXP && allowOverlapYN && allowOverlapZN) child = childPNN;
					//else if (allowOverlapXP && allowOverlapYN && allowOverlapZP) child = childPNP;
					//else if (allowOverlapXP && allowOverlapYP && allowOverlapZN) child = childPPN;
					//else /*if (allowOverlapXP && allowOverlapYP && allowOverlapZP)*/ child = childPPP;

					if (bounding.isInside(
							allowOverlapXN ? Float.NEGATIVE_INFINITY : child.minX,
							allowOverlapYN ? Float.NEGATIVE_INFINITY : child.minY,
							allowOverlapZN ? Float.NEGATIVE_INFINITY : child.minZ,
							allowOverlapXP ? Float.POSITIVE_INFINITY : child.minX + halfSize,
							allowOverlapYP ? Float.POSITIVE_INFINITY : child.minY + halfSize,
							allowOverlapZP ? Float.POSITIVE_INFINITY : child.minZ + halfSize
					                     )) {

						child.add(element, bounding, containingNodes, allowSplitX, allowSplitY, allowSplitZ,
						          allowOverlapXP, allowOverlapXN, allowOverlapYP, allowOverlapYN, allowOverlapZP, allowOverlapZN
						         );

					} else {
						elements.add(element);
						containingNodes.add(this);
					}

				}

			}

		}

		public void remove(Object o) {
			elements.remove(o);
			cleanup();
		}

		public void collectElements(List<T> collectedElements, Bounding bounding, boolean force) {
			if (force || bounding.overlaps(minX, minY, minZ, minX + size, minY + size, minZ + size)) {
				for (T element : elements) {
					LookupEntry lookupEntry = lookup.get(element);
					if (!lookupEntry.visitedFlag) {
						collectedElements.add(element);
						lookupEntry.visitedFlag = true;
					}
				}

				if (!isLeaf) {
					if (force || bounding.hasInside(minX, minY, minZ, minX + size, minY + size, minZ + size)) force = true;

					childNNN.collectElements(collectedElements, bounding, force);
					childNNP.collectElements(collectedElements, bounding, force);
					childNPN.collectElements(collectedElements, bounding, force);
					childNPP.collectElements(collectedElements, bounding, force);
					childPNN.collectElements(collectedElements, bounding, force);
					childPNP.collectElements(collectedElements, bounding, force);
					childPPN.collectElements(collectedElements, bounding, force);
					childPPP.collectElements(collectedElements, bounding, force);
				}
			}
		}

		private Node getChildAt(float x, float y, float z) {
			if (x < minX + halfSize) {
				if (y < minY + halfSize) {
					if (z < minZ + halfSize) return childNNN;
					else return childNNP;
				} else {
					if (z < minZ + halfSize) return childNPN;
					else return childNPP;
				}
			} else {
				if (y < minY + halfSize) {
					if (z < minZ + halfSize) return childPNN;
					else return childPNP;
				} else {
					if (z < minZ + halfSize) return childPPN;
					else return childPPP;
				}
			}
		}

		private void createRootAt(float x, float y, float z) {
			Node newChildNNN = null;
			Node newChildNNP = null;
			Node newChildNPN = null;
			Node newChildNPP = null;
			Node newChildPNN = null;
			Node newChildPNP = null;
			Node newChildPPN = null;
			Node newChildPPP = null;

			float parentX, parentY, parentZ;

			if (x < minX + halfSize) {
				parentX = minX - size;
				if (y < minY + halfSize) {
					parentY = minY - size;
					if (z < minZ + halfSize) {
						parentZ = minZ - size;
						newChildPPP = this;
					} else {
						parentZ = minZ;
						newChildPPN = this;
					}
				} else {
					parentY = minY;
					if (z < minZ + halfSize) {
						parentZ = minZ - size;
						newChildPNP = this;
					} else {
						parentZ = minZ;
						newChildPNN = this;
					}
				}
			} else {
				parentX = minX;
				if (y < minY + halfSize) {
					parentY = minY - size;
					if (z < minZ + halfSize) {
						parentZ = minZ - size;
						newChildNPP = this;
					} else {
						parentZ = minZ;
						newChildNPN = this;
					}
				} else {
					parentY = minY;
					if (z < minZ + halfSize) {
						parentZ = minZ - size;
						newChildNNP = this;
					} else {
						parentZ = minZ;
						newChildNNN = this;
					}
				}
			}

			if (newChildNNN == null) newChildNNN = new Node(depth, size, parentX, parentY, parentZ);
			if (newChildNNP == null) newChildNNP = new Node(depth, size, parentX, parentY, parentZ + size);
			if (newChildNPN == null) newChildNPN = new Node(depth, size, parentX, parentY + size, parentZ);
			if (newChildNPP == null) newChildNPP = new Node(depth, size, parentX, parentY + size, parentZ + size);
			if (newChildPNN == null) newChildPNN = new Node(depth, size, parentX + size, parentY, parentZ);
			if (newChildPNP == null) newChildPNP = new Node(depth, size, parentX + size, parentY, parentZ + size);
			if (newChildPPN == null) newChildPPN = new Node(depth, size, parentX + size, parentY + size, parentZ);
			if (newChildPPP == null) newChildPPP = new Node(depth, size, parentX + size, parentY + size, parentZ + size);

			parent = new Node(
					depth - 1, size * 2, parentX, parentY, parentZ,
					newChildNNN, newChildNNP, newChildNPN, newChildNPP, newChildPNN, newChildPNP, newChildPPN, newChildPPP
			);

			SpaceOctree.this.root = parent;

		}

		private void split() {
			isLeaf = false;

			childNNN = new Node(this, halfSize, minX, minY, minZ);
			childNNP = new Node(this, halfSize, minX, minY, minZ + halfSize);
			childNPN = new Node(this, halfSize, minX, minY + halfSize, minZ);
			childNPP = new Node(this, halfSize, minX, minY + halfSize, minZ + halfSize);
			childPNN = new Node(this, halfSize, minX + halfSize, minY, minZ);
			childPNP = new Node(this, halfSize, minX + halfSize, minY, minZ + halfSize);
			childPPN = new Node(this, halfSize, minX + halfSize, minY + halfSize, minZ);
			childPPP = new Node(this, halfSize, minX + halfSize, minY + halfSize, minZ + halfSize);

			Set<T> oldElements = elements;
			elements = new HashSet<>();

			for (T oldElement : oldElements) {
				List<Node> containingNodes = lookup.get(oldElement).containingNodes;

				for (Node containingNode : containingNodes) {
					containingNode.elements.remove(oldElement);
				}

				lookup.remove(oldElement);

			}

			for (T oldElement : oldElements) {
				add(oldElement, boundingGetter.apply(oldElement));
			}

		}

		public void cleanup() {

			if (!isLeaf && elements.isEmpty()
					&& childNNN.isLeaf && childNNN.elements.isEmpty()
					&& childNNP.isLeaf && childNNP.elements.isEmpty()
					&& childNPN.isLeaf && childNPN.elements.isEmpty()
					&& childNPP.isLeaf && childNPP.elements.isEmpty()
					&& childPNN.isLeaf && childPNN.elements.isEmpty()
					&& childPNP.isLeaf && childPNP.elements.isEmpty()
					&& childPPN.isLeaf && childPPN.elements.isEmpty()
					&& childPPP.isLeaf && childPPP.elements.isEmpty()
					) {

				childNNN = null;
				childNNP = null;
				childNPN = null;
				childNPP = null;
				childPNN = null;
				childPNP = null;
				childPPN = null;
				childPPP = null;

				isLeaf = true;

			}

			if (parent != null && parent.elements.isEmpty()) {
				parent.cleanup();
			}

		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			for (int i = root.depth; i < depth; i++) sb.append("\t");

			sb.append("elements: ").append(elements.size())
					.append(", size: ").append(size)
					.append(", minX: ").append(minX)
					.append(", minY: ").append(minY)
					.append(", minZ: ").append(minZ)
					.append("\n");

			if (!isLeaf) {
				sb.append(childNNN.toString());
				sb.append(childNNP.toString());
				sb.append(childNPN.toString());
				sb.append(childNPP.toString());
				sb.append(childPNN.toString());
				sb.append(childPNP.toString());
				sb.append(childPPN.toString());
				sb.append(childPPP.toString());
			}

			return sb.toString();

		}

		public long nodeCount() {
			long i = elements.size();

			if (!isLeaf) {
				i += childNNN.nodeCount();
				i += childNNP.nodeCount();
				i += childNPN.nodeCount();
				i += childNPP.nodeCount();
				i += childPNN.nodeCount();
				i += childPNP.nodeCount();
				i += childPPN.nodeCount();
				i += childPPP.nodeCount();
			}

			return i;
		}
	}

	private class OctreeIterator implements Iterator<T> {

		private Iterator<Map.Entry<T, LookupEntry>> inner;
		private Map.Entry<T, LookupEntry>           current;

		public OctreeIterator(Iterator<Map.Entry<T, LookupEntry>> inner) {
			this.inner = inner;
		}

		@Override
		public boolean hasNext() {
			return inner.hasNext();
		}

		@Override
		public T next() {
			current = inner.next();
			return current.getKey();
		}

		@Override
		public void remove() {
			inner.remove();

			for (Node node : current.getValue().containingNodes) {
				node.remove(current.getKey());
			}

			current = null;
		}

	}

	private class LookupEntry {

		public boolean visitedFlag = false;
		public List<Node> containingNodes;

		public LookupEntry(List<Node> containingNodes) {
			this.containingNodes = containingNodes;
		}
	}

	private Node                  root;
	private Function<T, Bounding> boundingGetter;

	private Map<T, LookupEntry> lookup;

	public SpaceOctree(Function<T, Bounding> boundingGetter) {
		this(boundingGetter, 64, 0.1f);
	}

	public SpaceOctree(Function<T, Bounding> boundingGetter, int maxLeafElements, float minNodeSize) {
		lookup = new HashMap<>();
		this.boundingGetter = boundingGetter;

		clear();
		MAX_LEAF_ELEMENTS = maxLeafElements;
		MIN_NODE_SIZE = minNodeSize;
	}

	private void addLookup(T t, List<Node> containingNodes) {
		if (lookup.containsKey(t)) {
			lookup.get(t).containingNodes = containingNodes;
		}
		lookup.put(t, new LookupEntry(containingNodes));
	}

	public boolean update(T t) {
		if (lookup.containsKey(t)) {
			List<Node> nodes = lookup.remove(t).containingNodes;
			for (Node node : nodes) node.elements.remove(t);
			nodes.get(0).add(t, boundingGetter.apply(t));
			for (Node node : nodes) node.cleanup();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Filters the elements in this octree with a bounding.
	 * This method can return elements that are not overlapping the bounding.
	 *
	 * @param collectedElements the list for collected elements
	 * @param bounding          the bounding for filtering
	 * @return a list containing all elements intersecting with the bounding
	 */
	public List<T> getFiltered(List<T> collectedElements, Bounding bounding) {
		collectedElements.clear();

		root.collectElements(collectedElements, bounding, false);
		for (T collectedElement : collectedElements) {
			lookup.get(collectedElement).visitedFlag = false;
		}

		return collectedElements;
	}

	/**
	 * Filters the elements in this octree with a bounding.
	 *
	 * @param collectedElements the list for collected elements
	 * @param bounding          the bounding for filtering
	 * @return a list containing all elements intersecting with the bounding
	 */
	public List<T> getFilteredExact(List<T> collectedElements, Bounding bounding) {
		collectedElements.clear();

		root.collectElements(collectedElements, bounding, false);
		for (T collectedElement : collectedElements) {
			lookup.get(collectedElement).visitedFlag = false;
		}

		collectedElements.removeIf(b -> !boundingGetter.apply(b).overlapsBounding(bounding));

		return collectedElements;
	}

	@Override
	public int size() { return lookup.size(); }

	@Override
	public boolean isEmpty() { return size() == 0; }

	@Override
	public boolean contains(Object o) {
		return lookup.containsKey(o);
	}

	@Override
	public Iterator<T> iterator() {
		return new OctreeIterator(lookup.entrySet().iterator());
	}

	@Override
	public Object[] toArray() {
		return toArray(new Object[0]);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> A[] toArray(A[] a) {
		if (a.length < size()) {
			a = (A[]) Array.newInstance(a.getClass().getComponentType(), size());
		}

		int i = 0;
		for (T t : this) {
			a[i++] = (A) t;
		}

		if (i < a.length) {
			a[i] = null;
		}

		return a;
	}

	@Override
	public boolean add(T t) {
		remove(t);
		return root.add(t, boundingGetter.apply(t));
	}

	@Override
	public boolean remove(Object o) {
		if (lookup.containsKey(o)) {
			List<Node> nodes = lookup.remove(o).containingNodes;
			for (Node node : nodes) node.remove(o);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;

		for (T o : c) {
			changed |= add(o);
		}

		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;

		for (Object o : c) {
			changed |= remove(o);
		}

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Iterator<T> iterator = iterator();

		boolean changed = false;

		while (iterator.hasNext()) {
			T t = iterator.next();

			if (!c.contains(t)) {
				iterator.remove();
				changed = true;
			}
		}

		return changed;
	}

	@Override
	public void clear() {
		lookup.clear();
		root = new Node(0, 1, 0, 0, 0);
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
