package de.nerogar.noise.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import de.nerogar.noise.Noise;

/**
 * Utility class for easy program loading. Different preprocessor statements are defined:
 * <p>
 * <ul>
 * <li>{@code #include fileID}	(Includes the specified file at this position. fileID is a file id (see below))
 * <li>{@code #parameter foo}	(takes "foo" as the key for looking up the file path in the parameter list.)
 * </ul>
 * 
 * File IDs:
 * <ol>
 * <li>{@code foo/bar.glsl}		(specifies a file relatife to the calling file)
 * <li>{@code (foo/bar.glsl)}	(specifies a file relative to the program execution path)
 * <li>{@code <foo/bar.glsl>}	(specifies a file from the default noise engine shader library)
 * </ol>
 */
public class ProgramLoader {

	private static final Map<String, String> EMPTY_PARAMETERS = new HashMap<String, String>();

	public static String decodeFilename(String parent, String id) {
		if (id.startsWith("<")) {
			id = id.substring(1, id.indexOf(">"));

			return Noise.RESSOURCE_DIR + "shaders/" + id;
		}
		if (id.startsWith("(")) {
			id = id.substring(1, id.indexOf(")"));

			return id;
		} else {
			if (parent != null) {
				id = parent + "/" + id;
			}

			return id;
		}
	}

	public static String readFile(String filename, Map<String, String> parameters) {
		if (parameters == null) parameters = EMPTY_PARAMETERS;

		filename = filename.replaceAll("\\\\", "/"); //replace \ with /

		File file = new File(filename);
		String folder = file.getParent();

		StringBuilder text = new StringBuilder();

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(filename));
			String line;
			int lineNumber = 1;

			while ((line = fileReader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("#include ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(9);

					String nextFilename = decodeFilename(folder, line);

					text.append("#line 1\n");
					text.append(readFile(nextFilename, parameters)).append('\n');
					text.append("#line ").append(lineNumber + 1).append('\n');
				} else if (line.startsWith("#pinclude ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(10);

					String parameter = readFile(decodeFilename(folder, parameters.get(line.trim())), parameters);

					text.append("#line 1\n");
					text.append(parameter).append('\n');
					text.append("#line ").append(lineNumber + 1).append('\n');
				} else if (line.startsWith("#parameter ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(11).trim();

					String parameter = parameters.get(line);

					text.append(parameter).append('\n');
				} else {
					text.append(line).append('\n');
				}

				lineNumber++;
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		text.append("\n");

		Logger.log(Logger.INFO, "loaded program: " + filename);

		return text.toString();
	}

}
