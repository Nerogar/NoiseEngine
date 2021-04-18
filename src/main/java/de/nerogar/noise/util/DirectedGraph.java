package de.nerogar.noise.util;

import de.nerogar.noiseInterface.util.IDirectedGraph;

import java.util.*;

public class DirectedGraph<T> implements IDirectedGraph<T> {

	private HashSet<T>         nodes;
	private Map<T, HashSet<T>> outboundEdges;
	private Map<T, HashSet<T>> inboundEdges;

	public DirectedGraph() {
		nodes = new HashSet<>();
		outboundEdges = new HashMap<>();
		inboundEdges = new HashMap<>();
	}

	@Override
	public void addNode(T node) {
		nodes.add(node);
	}

	@Override
	public void removeNode(T node) {
		if (nodes.remove(node)) {
			for (T neighbor : getInboundEdges(node)) {
				getOutboundEdges(neighbor).remove(node);
			}
			for (T neighbor : getOutboundEdges(node)) {
				getInboundEdges(neighbor).remove(node);
			}

			inboundEdges.remove(node);
			outboundEdges.remove(node);
		}
	}

	@Override
	public void addEdge(T from, T to) {
		getOutboundEdges(from).add(to);
		getInboundEdges(to).add(from);
	}

	@Override
	public void removeEdge(T from, T to) {
		getOutboundEdges(from).remove(to);
		getInboundEdges(to).remove(from);
	}

	@Override
	public Set<T> getOutboundEdges(T node) {
		if (!nodes.contains(node)) {
			throw new IllegalArgumentException("node not found");
		}

		return outboundEdges.computeIfAbsent(node, t -> new HashSet<>());
	}

	@Override
	public Set<T> getInboundEdges(T node) {
		if (!nodes.contains(node)) {
			throw new IllegalArgumentException("node not found");
		}

		return inboundEdges.computeIfAbsent(node, t -> new HashSet<>());
	}

	@Override
	public boolean isAcyclic() {
		boolean cycleFound = false;
		Map<T, Boolean> visited = new HashMap<>();
		Deque<T> stack = new ArrayDeque<>();

		for (T node : nodes) {
			visited.put(node, false);
		}

		loop:
		for (T root : nodes) {
			stack.push(root);

			while (!stack.isEmpty()) {
				T current = stack.peek();

				visited.put(current, true);

				for (T next : getOutboundEdges(current)) {
					if (!visited.get(next)) {
						// if the next node was not visited, add it to the path
						stack.push(next);
					} else if (stack.contains(next)) {
						// else: if the stack contains the next node, we found a cycle
						cycleFound = true;
						break loop;
					}
				}

				// if no new root was added, a dea end was found
				if (current == stack.peek()) {
					stack.pop();
				}
			}
		}

		return !cycleFound;
	}

	/**
	 * Creates a topologically sorted list of nodes in this graph.
	 * The list is sorted so that for each edge (N1, N2) the index of N2 is greater that the index of N1.
	 *
	 * @return a topologically sorted list of the nodes in this graph
	 */
	@Override
	public List<T> getTopologicalSort() {
		List<T> topologicalList = new ArrayList<>();

		Deque<T> queue = new ArrayDeque<>();
		Map<T, Integer> unvisitedInboundEdges = new HashMap<>();

		for (T node : nodes) {
			int inboundEdgeCount = getInboundEdges(node).size();
			if (inboundEdgeCount == 0) {
				queue.add(node);
			}
			unvisitedInboundEdges.put(node, inboundEdgeCount);
		}

		while (!queue.isEmpty()) {
			T node = queue.poll();

			topologicalList.add(node);

			for (T neighbor : getOutboundEdges(node)) {
				int neighborInboundEdges = unvisitedInboundEdges.get(neighbor) - 1;
				unvisitedInboundEdges.put(neighbor, neighborInboundEdges);

				if (neighborInboundEdges == 0) {
					queue.add(neighbor);
				}
			}
		}

		if (topologicalList.size() != nodes.size()) {
			throw new IllegalStateException("Could not create topological order. Graph is not acyclic.");
		}

		return topologicalList;
	}
}
