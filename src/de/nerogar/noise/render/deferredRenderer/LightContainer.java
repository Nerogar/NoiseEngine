package de.nerogar.noise.render.deferredRenderer;

import java.util.ArrayList;
import java.util.List;

public class LightContainer {

	private List<Light> lights;

	public LightContainer() {
		lights = new ArrayList<Light>();
	}

	public void addLight(Light light) {
		lights.add(light);
	}

	public void update(float timeDelta) {
		for (int i = lights.size() - 1; i >= 0; i--) {
			if (lights.get(i).dead()) lights.remove(i);
		}
	}

	public List<Light> getLights() {
		return lights;
	}

}
