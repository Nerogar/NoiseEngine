package de.nerogar.noise.debug;

import java.util.*;

import de.nerogar.noise.util.Color;

public class Profiler {

	private String name;

	private int currentIndex;
	private int historySize;

	private Map<Integer, ProfilerStatisticsCollection> collections;
	private ArrayList<Integer> values;

	public Profiler(String name) {
		this.name = name;
		collections = new HashMap<Integer, ProfilerStatisticsCollection>();

		historySize = 512;

		currentIndex = 0;

		values = new ArrayList<Integer>();
	}

	public String getName() {
		return name;
	}

	protected void registerProperty(int id, int collectionID, Color color, String name) {
		ProfilerStatistic statistic = new ProfilerStatistic(name, id, color, historySize);

		ProfilerStatisticsCollection collection = collections.get(collectionID);
		if (collection == null) {
			collection = new ProfilerStatisticsCollection();
			collections.put(collectionID, collection);
		}

		collection.addStatistic(statistic);

		//ensure values size
		for (int i = values.size(); i <= id; i++) {
			values.add(0);
		}
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

	public int getHistorySize() {
		return historySize;
	}

	public Collection<ProfilerStatisticsCollection> getProfilerCollections() {
		return collections.values();
	}

	public void reset() {
		currentIndex++;
		currentIndex %= historySize;

		for (int id = 0; id < values.size(); id++) {
			search: for (ProfilerStatisticsCollection collection : collections.values()) {
				for (ProfilerStatistic statistic : collection.statisticList) {
					if (statistic.id == id) {
						statistic.history.set(currentIndex, values.get(id));
						statistic.firstIndex = currentIndex + 1;

						collection.maxHistory = Math.max(collection.maxHistory, values.get(id));
						break search;
					}
				}
			}
		}

		ArrayList<Integer> newValues = new ArrayList<Integer>();

		for (int i = 0; i < values.size(); i++) {
			newValues.add(values.get(i));
		}

		values = newValues;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[Profiler: ").append(name).append("]\n");

		for (ProfilerStatisticsCollection collection : collections.values()) {
			for (ProfilerStatistic statistic : collection.statisticList) {
				sb.append("\t").append(statistic.name).append(": ").append(statistic.getValue(historySize - 1)).append("\n");
			}
		}

		return sb.toString();
	}

}
