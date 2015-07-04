package de.nerogar.noise.render.deferredRenderer;

import java.util.HashSet;
import java.util.Set;

public class LightContainer {

	private Set<Light> lights;

	public LightContainer() {
		lights = new HashSet<Light>();
	}

	public void addLight(Light light) {
		lights.add(light);
	}

	public void removeLight(Light light) {
		lights.remove(light);
	}

	public Set<Light> getLights() {
		return lights;
	}

}
