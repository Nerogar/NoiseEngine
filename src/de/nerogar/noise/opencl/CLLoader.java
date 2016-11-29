package de.nerogar.noise.opencl;

import java.util.Map;

import de.nerogar.noise.util.FileUtil;
import de.nerogar.noise.util.ProgramLoader;

/**
 * Utility class for easy openCL program loading. Special syntax can be used as defined in {@link ProgramLoader ProgramLoader}.
 */
public class CLLoader {

	/**
	 * Loads an openCL program from files.
	 * All file paths are {@link ProgramLoader file IDs}.
	 * 
	 * @param clContext the openCL context to create the program in
	 * @param clProgramFile path to the source file
	 * @return the new openCL program
	 */
	public static CLProgram loadCLProgram(CLContext clContext, String clProgramFile) {
		return loadCLProgram(clContext, clProgramFile, (Map<String, String>) null);
	}

	/**
	 * Loads an openCL program from files.
	 * All file paths are {@link ProgramLoader file IDs}.
	 * More info on parameters {@link ProgramLoader here}.
	 * 
	 * @param clContext the openCL context to create the program in
	 * @param clProgramFile path to the source file
	 * @param parameters a map containing all parameters
	 * @return the new openCL program
	 */
	public static CLProgram loadCLProgram(CLContext clContext, String clProgramFile, Map<String, String> parameters) {
		String clProgramSource = ProgramLoader.readFile(FileUtil.decodeFilename(null, clProgramFile), parameters);

		CLProgram clProgram = new CLProgram(clContext, clProgramSource);

		return clProgram;
	}
}
