package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

class ProfilerStatisticsCollection {
	protected List<ProfilerStatistic> statisticList;

	protected int maxHistory;

	public ProfilerStatisticsCollection() {
		statisticList = new ArrayList<ProfilerStatistic>();
		
		maxHistory = 1;
	}

	public void addStatistic(ProfilerStatistic statistic) {
		statisticList.add(statistic);
	}
}