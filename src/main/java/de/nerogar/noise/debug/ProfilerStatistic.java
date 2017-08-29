package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

import de.nerogar.noise.util.Color;

class ProfilerStatistic {
	protected String name;
	protected int id;
	protected List<Integer> history;
	protected Color color;
	protected int firstIndex;

	public ProfilerStatistic(String name, int id, Color color, int historySize) {
		super();
		this.name = name;
		this.id = id;
		this.history = new ArrayList<Integer>();
		this.color = color;

		for (int i = 0; i < historySize; i++) {
			history.add(0);
		}
	}

	public int getValue(int position) {
		position = (position + firstIndex) % history.size();
		return history.get(position);
	}
}