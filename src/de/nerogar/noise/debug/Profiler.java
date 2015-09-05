package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

public class Profiler {

	private String name;

	private List<ArrayList<Integer>> history;
	private int currentIndex;

	private ArrayList<Integer> values;
	private ArrayList<Integer> minMaxHistory;
	private ArrayList<String> names;

	private boolean running;

	public Profiler(String name, int historyLength) {
		this.name = name;

		running = true;

		history = new ArrayList<ArrayList<Integer>>();
		currentIndex = historyLength - 1;

		for (int i = 0; i < historyLength; i++) {
			history.add(new ArrayList<Integer>());
		}

		values = new ArrayList<Integer>();
		minMaxHistory = new ArrayList<Integer>();
		names = new ArrayList<String>();
	}

	protected void registerName(int id, int maxValue, String name) {

		//ensure size
		while (names.size() < id + 1) {
			values.add(0);
			minMaxHistory.add(0);
			names.add("");
		}

		names.set(id, name);
		minMaxHistory.set(id, maxValue);
	}

	public void setValue(int id, int value) {
		if (running) values.set(id, value);
	}

	public void incrementValue(int id) {
		if (running) values.set(id, values.get(id) + 1);
	}

	public void decrementValue(int id) {
		if (running) values.set(id, values.get(id) - 1);
	}

	public void addValue(int id, int newValue) {
		if (running) values.set(id, values.get(id) + newValue);
	}

	public int getPropertyCount() {
		return names.size();
	}

	public int getHistoryLength() {
		return history.size();
	}

	public String getName(int id) {
		return names.get(id);
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
		int max = minMaxHistory.get(id);

		for (int i = 0; i < history.size(); i++) {
			List<Integer> timeSample = history.get((i + currentIndex + 1) % history.size());
			if (!timeSample.isEmpty()) {
				max = Math.max(max, timeSample.get(id));
			}

		}

		return max;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void reset() {
		currentIndex++;
		currentIndex %= history.size();

		history.set(currentIndex, values);

		values = new ArrayList<Integer>();

		for (int i = 0; i < names.size(); i++) {
			values.add(history.get(currentIndex).get(i));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[Profiler: ").append(name).append("]\n");
		ArrayList<Integer> currentValues = history.get(currentIndex);

		for (int i = 0; i < names.size(); i++) {
			if (names.get(i) != null && !names.get(i).isEmpty() && i < currentValues.size()) {
				sb.append("\t").append(names.get(i)).append(": ").append(currentValues.get(i)).append("\n");
			}
		}

		return sb.toString();
	}

}
