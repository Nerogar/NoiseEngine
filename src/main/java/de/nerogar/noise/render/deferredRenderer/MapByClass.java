package de.nerogar.noise.render.deferredRenderer;

import java.util.*;
import java.util.function.Consumer;

public class MapByClass<T> {

	private final Map<Class<?>, List<T>> map;
	private final Consumer<T>            adder;

	public MapByClass() {
		this.map = new HashMap<>();
		this.adder = this::add;
	}

	public void add(T t) {
		Class<?> tClass = t.getClass();
		List<T> list = map.computeIfAbsent(tClass, k -> new ArrayList<>());
		list.add(t);
	}

	public void clear() {
		for (List<T> value : map.values()) {
			value.clear();
		}
	}

	public Collection<List<T>> getLists() {
		return map.values();
	}

	public Consumer<T> getAdder() {
		return adder;
	}
}
