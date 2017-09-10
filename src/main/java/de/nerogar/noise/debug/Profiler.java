package de.nerogar.noise.debug;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Profiler {

	private static final int HISTORY_SIZE = Noise.getSettings().getObject("profiler").getInt("historySize");

	private String name;

	protected boolean autoUpdate;

	private int currentIndex;
	private int historySize;

	private Map<Integer, ProfilerStatisticsCategory> categories;
	private ArrayList<Integer>                       values;

	public Profiler(String name, boolean autoUpdate) {
		this.name = name;
		this.autoUpdate = autoUpdate;

		categories = new HashMap<>();

		historySize = HISTORY_SIZE;

		currentIndex = 0;

		values = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	protected void registerProperty(int id, int categoryID, Color color, String name) {
		ProfilerStatistic statistic = new ProfilerStatistic(name, id, color, historySize);

		ProfilerStatisticsCategory category = categories.get(categoryID);
		if (category == null) {
			category = new ProfilerStatisticsCategory();
			categories.put(categoryID, category);
		}

		category.addStatistic(statistic);

		// ensure values size
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

	public Collection<ProfilerStatisticsCategory> getProfilerCategories() {
		return categories.values();
	}

	public void reset() {
		currentIndex++;
		currentIndex %= historySize;

		for (int id = 0; id < values.size(); id++) {
			search:
			for (ProfilerStatisticsCategory category : categories.values()) {
				for (ProfilerStatistic statistic : category.statisticList) {
					if (statistic.id == id) {
						statistic.history.set(currentIndex, values.get(id));
						statistic.firstIndex = currentIndex + 1;

						category.maxHistory = Math.max(category.maxHistory, values.get(id));
						break search;
					}
				}
			}
		}

		/*ArrayList<Integer> newValues = new ArrayList<>();

		for (Integer value : values) {
			newValues.add(value);
		}

		values = newValues;*/
	}

	public void resetMax() {
		for (ProfilerStatisticsCategory category : categories.values()) {

			int max = 0;

			for (ProfilerStatistic profilerStatistic : category.statisticList) {
				for (Integer historyValue : profilerStatistic.history) {
					max = Math.max(max, historyValue);
				}
			}

			category.maxHistory = max;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[Profiler: ").append(name).append("]\n");

		for (ProfilerStatisticsCategory category : categories.values()) {
			for (ProfilerStatistic statistic : category.statisticList) {
				sb.append("\t").append(statistic.name).append(": ").append(statistic.getValue(historySize - 1)).append("\n");
			}
		}

		return sb.toString();
	}

}
