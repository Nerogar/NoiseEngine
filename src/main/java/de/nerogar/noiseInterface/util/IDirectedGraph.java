package de.nerogar.noiseInterface.util;

import java.util.List;
import java.util.Set;

public interface IDirectedGraph<T> {

	void addNode(T node);

	void removeNode(T node);

	void addEdge(T from, T to);

	void removeEdge(T from, T to);

	Set<T> getOutboundEdges(T node);

	Set<T> getInboundEdges(T node);

	boolean isAcyclic();

	List<T> getTopologicalSort();
}
