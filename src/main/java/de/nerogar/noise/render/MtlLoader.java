package de.nerogar.noise.render;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.file.ResourceDescriptor;
import de.nerogar.noise.util.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MtlLoader {

	public static List<Material> load(List<ResourceDescriptor> files) {
		Map<String, Material> materialMap = new HashMap<>();

		for (ResourceDescriptor file : files) {
			List<Material> newMaterials = load(file);

			for (Material material : newMaterials) {
				if (!materialMap.containsKey(material.getName())) {
					materialMap.put(material.getName(), material);
				}
			}
		}

		return new ArrayList<>(materialMap.values());
	}

	public static List<Material> load(ResourceDescriptor file) {
		List<Material> materials = new ArrayList<>();

		Material currentMaterial = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.asStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				String[] lineSplit = line.split(" ");

				switch (lineSplit[0]) {
					case "newmtl":
						if (currentMaterial != null) {
							materials.add(currentMaterial);
						}
						currentMaterial = new Material();
						currentMaterial.setName(lineSplit[1]);
						break;
					case "Ka":
						if (lineSplit[1].equals("spectral") || lineSplit[1].equals("xyz")){
							throw new RuntimeException("spectral curves and the CIEXYZ color space are not supported");
						}
						currentMaterial.setAlbedoColor(new Color(Float.parseFloat(lineSplit[1]), Float.parseFloat(lineSplit[2]), Float.parseFloat(lineSplit[3]), 0));
						break;
					/*case "Kd":
						if (lineSplit[1].equals("spectral") || lineSplit[1].equals("xyz")){
							throw new RuntimeException("spectral curves and the CIEXYZ color space are not supported");
						}
						currentMaterial.setSpecularTintColor(new Color(Float.parseFloat(lineSplit[1]), Float.parseFloat(lineSplit[2]), Float.parseFloat(lineSplit[3]), 0));
						break;*/
					case "Ke":
						if (lineSplit[1].equals("spectral") || lineSplit[1].equals("xyz")){
							throw new RuntimeException("spectral curves and the CIEXYZ color space are not supported");
						}
						currentMaterial.setEmissionColor(new Color(Float.parseFloat(lineSplit[1]), Float.parseFloat(lineSplit[2]), Float.parseFloat(lineSplit[3]), 0));
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (currentMaterial != null) {
			materials.add(currentMaterial);
		}

		return materials;
	}

}
