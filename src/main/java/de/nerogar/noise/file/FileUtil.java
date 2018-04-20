package de.nerogar.noise.file;

import de.nerogar.noise.Noise;

public class FileUtil {

	public static final String SHADER_SUBFOLDER  = "shaders/";
	public static final String TEXTURE_SUBFOLDER = "textures/";
	public static final String MESH_SUBFOLDER    = "meshes/";
	public static final String DATA_SUBFOLDER    = "data/";

	public static String getCanonicalPath(String filename) {
		filename = filename.replaceAll("\\\\", "/"); //replace \ with /

		return filename;
	}

	/**
	 * Returns a ResourceDescriptor for a fileID
	 * <p>
	 * <li>{@code foo/bar.txt}     (specifies a file relative to the program execution path)
	 * <li>{@code (foo/bar.txt)}   (specifies a file relative to the program execution path)
	 * <li>{@code <foo/bar.txt>}   (specifies a file from the default noise engine library)
	 *
	 * @param id the file id
	 * @return a {@link ResourceDescriptor ResourceDescriptor}
	 */
	public static ResourceDescriptor get(String id) {
		return get(id, "");
	}

	/**
	 * Returns a ResourceDescriptor for a fileID
	 * <p>
	 * <li>{@code foo/bar.txt}     (specifies a file relative to the program execution path)
	 * <li>{@code (foo/bar.txt)}   (specifies a file relative to the program execution path)
	 * <li>{@code <foo/bar.txt>}   (specifies a file from the default noise engine library)
	 *
	 * @param id        the file id
	 * @param subFolder the sub folder inside the noise library
	 * @return a {@link ResourceDescriptor ResourceDescriptor}
	 */
	public static ResourceDescriptor get(String id, String subFolder) {
		if (id.startsWith("<")) {
			id = id.substring(1, id.indexOf(">"));

			if (Noise.RESOURCE_DIR == null) {
				id = Noise.DEFAULT_RESOURCE_DIR + subFolder + id;
				return new JarResourceDescriptor(id, subFolder);
			} else {
				id = Noise.RESOURCE_DIR + subFolder + id;
				return new FileResourceDescriptor(id, subFolder);
			}

		} else if (id.startsWith("(")) {
			id = id.substring(1, id.indexOf(")"));

			return new FileResourceDescriptor(id, subFolder);
		} else {
			return new FileResourceDescriptor(id, subFolder);
		}
	}

}
