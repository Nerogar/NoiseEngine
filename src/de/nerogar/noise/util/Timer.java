package de.nerogar.noise.util;

public class Timer {

	private class Average {
		private final int TIME_COUNT = 10;

		public double[] times = new double[TIME_COUNT];
		private int index = 0;

		public void addTime(double time) {
			times[index] = time;
			index++;
			index %= TIME_COUNT;
		}

		public double getAvg() {
			double sum = 0;
			for (int i = 0; i < TIME_COUNT; i++) {
				sum += times[i];
			}
			return sum * 0.1 * 1.1; //never underestimate sleep times
		}
	}

	private double firstFrame;
	private double lastFrame;

	private double calcTime;
	private double delta;

	private Average sleepAvg, yieldAvg;

	public Timer() {
		firstFrame = getTime();
		lastFrame = firstFrame;

		sleepAvg = new Average();
		yieldAvg = new Average();
	}

	private double getTime() {
		return System.nanoTime() * 0.000000001d;
	}

	/**
	 * Updates delta times. Set targetDelta to the time you want to synchronize with,
	 * or a value < 0 if you dont want to synchronize.
	 * 
	 * @param targetDelta the target time delta for synchronizing
	 */

	public void update(double targetDelta) {
		try {
			double startSync = getTime();
			calcTime = startSync - lastFrame;

			if (targetDelta > 0) {
				double sleepTimer = startSync;
				double currTime;

				while (sleepTimer - lastFrame < targetDelta - sleepAvg.getAvg()) {
					Thread.sleep(1);

					currTime = getTime();
					sleepAvg.addTime(currTime - sleepTimer);
					sleepTimer = currTime;
				}

				while (sleepTimer - lastFrame < targetDelta - yieldAvg.getAvg()) {
					Thread.yield();

					currTime = getTime();
					yieldAvg.addTime(currTime - sleepTimer);
					sleepTimer = currTime;
				}
			}

			double endSync = getTime();
			delta = endSync - lastFrame;

			lastFrame = endSync;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public float getCalcTime() {
		return (float) calcTime;
	}

	public float getDelta() {
		return (float) delta;
	}

	public float getFrequency() {
		return (float) (1.0 / delta);
	}

	public double getRuntime() {
		return lastFrame - firstFrame;
	}

}
