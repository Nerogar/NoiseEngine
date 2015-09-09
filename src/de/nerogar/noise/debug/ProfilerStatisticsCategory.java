package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

class ProfilerStatisticsCategory {
	protected List<ProfilerStatistic> statisticList;

	protected int maxHistory;

	public ProfilerStatisticsCategory() {
		statisticList = new ArrayList<ProfilerStatistic>();
		
		maxHistory = 1;
	}

	public void addStatistic(ProfilerStatistic statistic) {
		statisticList.add(statistic);
	}
}