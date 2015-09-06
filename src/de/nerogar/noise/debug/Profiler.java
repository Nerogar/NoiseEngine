package de.nerogar.noise.debug;

import java.util.*;

import de.nerogar.noise.util.Color;

public class Profiler {

	private String name;

	private List<ArrayList<Integer>> history;
	private int currentIndex;

	private ArrayList<String> names;
	private ArrayList<Integer> values;
	private ArrayList<Color> colors;
	private ArrayList<Integer> maxHistory;

	private List<Integer> propertyList;

	public Profiler(String name) {
		this.name = name;
		int historyLength = 512;

		history = new ArrayList<ArrayList<Integer>>();
		currentIndex = historyLength - 1;

		for (int i = 0; i < historyLength; i++) {
			history.add(new ArrayList<Integer>());
		}

		names = new ArrayList<String>();
		values = new ArrayList<Integer>();
		colors = new ArrayList<Color>();
		maxHistory = new ArrayList<Integer>();

		propertyList = new ArrayList<Integer>();
	}

	public String getName() {
		return name;
	}

	protected void registerProperty(int id, Color color, String name) {

		//ensure size
		while (names.size() < id + 1) {
			names.add(null);
			values.add(0);
			colors.add(null);
			maxHistory.add(0);
		}

		names.set(id, name);
		colors.set(id, color);

		propertyList.add(id);
	}

	public void setValue(int id, int value) {
		values.set(id, value);
	}

	public void incrementValue(int id) {
		values.set(id, values.get(id) + 1);
	}

	public void decrementValue(int id) {
		values.set(id, values.get(id) - 1);
	}

	public void addValue(int id, int newValue) {
		values.set(id, values.get(id) + newValue);
	}

	public List<Integer> getPropertyList() {
		return propertyList;
	}

	public int getHistoryLength() {
		return history.size();
	}

	public String getName(int id) {
		return names.get(id);
	}

	public Color getColor(int id) {
		return colors.get(id);
	}

	public int getValue(int id) {
		return history.get(currentIndex).get(id);
	}

	public ArrayList<Integer> getHistory(int id) {
		ArrayList<Integer> valueHistory = new ArrayList<Integer>();

		for (int i = 0; i < history.size(); i++) {
			List<Integer> timeSample = history.get((i + currentIndex + 1) % history.size());
			if (timeSample.isEmpty()) {
				valueHistory.add(0);
			} else {
				valueHistory.add(timeSample.get(id));
			}

		}

		return valueHistory;
	}

	public int getMaxHistory(int id) {
		return maxHistory.get(id);
	}

	public void reset() {
		currentIndex++;
		currentIndex %= history.size();

		history.set(currentIndex, values);

		values = new ArrayList<Integer>();

		for (int i = 0; i < names.size(); i++) {
			values.add(history.get(currentIndex).get(i));

			maxHistory.set(i, Math.max(maxHistory.get(i), values.get(i)));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[Profiler: ").append(name).append("]\n");
		ArrayList<Integer> currentValues = history.get(currentIndex);

		for (int id : propertyList) {
			sb.append("\t").append(names.get(id)).append(": ").append(currentValues.get(id)).append("\n");
		}

		return sb.toString();
	}

}
