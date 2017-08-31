package de.nerogar.noise.debug;

import de.nerogar.noise.util.Color;

public class JavaProfiler extends Profiler {

	private static final int MEMORY = 0;

	public static final int MEMORY_MAX_HEAP_SIZE = 0;
	public static final int MEMORY_HEAP_SIZE     = 1;
	public static final int MEMORY_USED_HEAP     = 2;

	public JavaProfiler() {
		super("java", true);

		registerProperty(MEMORY_MAX_HEAP_SIZE, MEMORY, new Color(0.8f, 0.0f, 0.1f, 1.0f), "max heap size");
		registerProperty(MEMORY_HEAP_SIZE, MEMORY, new Color(0.8f, 0.4f, 0.2f, 1.0f), "heap size");
		registerProperty(MEMORY_USED_HEAP, MEMORY, new Color(0.3f, 0.8f, 0.2f, 1.0f), "used memory");
	}

	@Override
	public void reset() {
		super.reset();

		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();
		long usedMemory = totalMemory - runtime.freeMemory();

		setValue(MEMORY_MAX_HEAP_SIZE, (int) (maxMemory/ 1024));
		setValue(MEMORY_HEAP_SIZE, (int) (totalMemory / 1024));
		setValue(MEMORY_USED_HEAP, (int) (usedMemory/ 1024));
	}

}
