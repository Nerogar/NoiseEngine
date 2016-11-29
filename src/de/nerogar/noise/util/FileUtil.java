package de.nerogar.noise.util;

import de.nerogar.noise.Noise;

public class FileUtil {

	public static final String SHADER_SUBFOLDER  = "shaders/";
	public static final String TEXTURE_SUBFOLDER = "textures/";

	/**
	 * Translates file IDs to filenames.
	 *
	 * <li>{@code foo/bar.txt}     (specifies a file relative to the program execution path)
	 * <li>{@code (foo/bar.txt)}   (specifies a file relative to the program execution path)
	 * <li>{@code <foo/bar.txt>}   (specifies a file from the default noise engine library)
	 *
	 * @param id the file id to translate
	 * @return the filename
	 */
	public static String decodeFilename(String id) {
		return decodeFilename(null, id);
	}

	/**
	 * Translates file IDs to filenames.
	 *
	 * <li>{@code foo/bar.txt}     (specifies a file relative to the calling file, or the program execution path, if parent is null)
	 * <li>{@code (foo/bar.txt)}   (specifies a file relative to the program execution path)
	 * <li>{@code <foo/bar.txt>}   (specifies a file from the default noise engine library)
	 *
	 * @param parent the calling file
	 * @param id the file id to translate
	 * @return the filename
	 */
	public static String decodeFilename(String parent, String id) {
		return decodeFilename(parent, "", id);
	}

	/**
	 * Translates file IDs to filenames.
	 *
	 * <li>{@code foo/bar.txt}     (specifies a file relative to the calling file, or the program execution path, if parent is null)
	 * <li>{@code (foo/bar.txt)}   (specifies a file relative to the program execution path)
	 * <li>{@code <foo/bar.txt>}   (specifies a file from the default noise engine library)
	 *
	 * @param parent the calling file
	 * @param subFolder the resource subfolder in the noiseEngine resource directory
	 * @param id the file id to translate
	 * @return the filename
	 */
	public static String decodeFilename(String parent, String subFolder, String id) {
		if (id.startsWith("<")) {
			id = id.substring(1, id.indexOf(">"));

			return Noise.RESSOURCE_DIR + subFolder + id;
		} else if (id.startsWith("(")) {
			id = id.substring(1, id.indexOf(")"));

			return id;
		} else {
			if (parent != null) {
				id = parent + "/" + id;
			}

			return id;
		}
	}

}
