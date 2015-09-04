package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.util.Logger;

public class DebugWindow {

	private GLWindow window;

	private List<Profiler> profilerList;

	public DebugWindow(Profiler... profiler) {
		profilerList = new ArrayList<Profiler>();

		for (Profiler p : profiler) {
			profilerList.add(p);
		}

		if (!Noise.DEBUG) return;

		window = new GLWindow("debug", 500, 300, true, 0, null, null);
	}

	public void update() {
		if (!Noise.DEBUG) return;
		if (window.shouldClose()) window.cleanup();
		Noise.getRessourceProfiler().setRunning(false);

		Logger.log(Logger.DEBUG, Noise.getRessourceProfiler().toString());

		Noise.getRessourceProfiler().setRunning(true);
	}

}
