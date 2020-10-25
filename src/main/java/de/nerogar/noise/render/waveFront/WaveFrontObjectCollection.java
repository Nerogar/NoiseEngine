package de.nerogar.noise.render.waveFront;

import de.nerogar.noise.file.ResourceDescriptor;
import de.nerogar.noise.render.Mesh;

import java.util.List;
import java.util.Map;

public class WaveFrontObjectCollection {

	public final List<ResourceDescriptor> materialFiles;

	public final Map<String, WaveFrontObject> objects;

	public WaveFrontObjectCollection(List<ResourceDescriptor> materialFiles, Map<String, WaveFrontObject> objects) {
		this.materialFiles = materialFiles;
		this.objects = objects;
	}

	public static class WaveFrontObject {

		public final Mesh   mesh;
		public final String material;

		public WaveFrontObject(Mesh mesh, String material) {
			this.mesh = mesh;
			this.material = material;
		}
	}
}
