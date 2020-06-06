package de.nerogar.noise.util;

import de.nerogar.noise.Noise;
import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.file.ResourceDescriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for easy program loading. Different preprocessor statements are defined:
 * <p>
 * <ul>
 * <li>{@code #include fileID}	(Includes the specified file at this position. fileID is a file id, specified in {@link FileUtil#get(String)})
 * <li>{@code #parameter foo}	(takes "foo" as the key for looking up the parameter in the parameter list and replaces the line with the parameter.)
 * <li>{@code #pinclude foo}	(takes "foo" as the key for looking up the file path in the parameter list and includes the file specified by the file path.)
 * </ul>
 * <p>
 * </ol>
 */
public class ProgramLoader {

	private static final Map<String, String> EMPTY_PARAMETERS = new HashMap<>();

	public static String readFile(ResourceDescriptor resourceDescriptor, Map<String, String> parameters) {
		if (parameters == null) parameters = EMPTY_PARAMETERS;

		InputStream inputStream = resourceDescriptor.asStream();

		StringBuilder text = new StringBuilder();

		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			int lineNumber = 1;

			while ((line = fileReader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("#include ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(9);

					ResourceDescriptor nextFile = resourceDescriptor.getRelative(line);

					text.append("#line 1\n");
					text.append(readFile(nextFile, parameters)).append('\n');
					text.append("#line ").append(lineNumber + 1).append('\n');
				} else if (line.startsWith("#pinclude ")) {
					int commentIndex = line.indexOf("//");
					if (commentIndex >= 0) line = line.substring(0, commentIndex);

					line = line.substring(10).trim();

					ResourceDescriptor nextFile = resourceDescriptor.getRelative(parameters.get(line));

					text.append("#line 1\n");
					text.append(readFile(nextFile, parameters)).append('\n');
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

		Noise.getLogger().log(Logger.INFO, "loaded program: " + resourceDescriptor.getFilename());

		return text.toString();
	}

}
