package de.nerogar.noise.util;

import de.nerogar.noise.log.Logger;

public class Timer {

	private long lastFrameTime;
	private float timeDelta;

	private int frameCount;
	private long lastFramePrint;

	private long startTime;
	private double runTime;

	public Timer() {
		startTime = System.nanoTime();
		lastFrameTime = startTime;
		lastFramePrint = startTime;
		runTime = 0D;
		timeDelta = 1F;
	}

	public void update() {
		long currentTime = System.nanoTime();
		timeDelta = (float) ((double) (currentTime - lastFrameTime) * 0.000000001D);

		if (currentTime - lastFramePrint > 1000000000L) {
			lastFramePrint += 1000000000L;
			Logger.log(Logger.DEBUG, "fps: " + frameCount + "; time: " + (1f / frameCount * 1000f) + "ms");
			frameCount = 0;
		}
		frameCount++;

		lastFrameTime = currentTime;
		runTime += (double) timeDelta;

	}

	public float getTimeDelta() {
		return timeDelta;
	}

	public double getRunTime() {
		return runTime;
	}

}
