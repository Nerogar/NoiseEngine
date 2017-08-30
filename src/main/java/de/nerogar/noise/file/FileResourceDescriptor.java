package de.nerogar.noise.file;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResourceDescriptor implements ResourceDescriptor {

	private String filename;
	private String subFolder;

	private String folder;

	public FileResourceDescriptor(String id, String subFolder) {
		this.filename = FileUtil.getCanonicalPath(id);
		this.subFolder = subFolder;

		folder = filename.substring(0, filename.lastIndexOf('/'));
	}

	@Override
	public InputStream asStream() {
		try {
			return new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			Noise.getLogger().log(Logger.WARNING, "could not load resource from file: " + filename);
			e.printStackTrace(Noise.getLogger().getWarningStream());
		}

		return null;
	}

	@Override
	public ResourceDescriptor getRelative(String id) {
		if (id.startsWith("<") || id.startsWith("(")) return FileUtil.get(id, subFolder);

		return new FileResourceDescriptor(new File(filename).getParent() + "/" + id, subFolder);
	}

	@Override
	public String getFilename() {
		return filename;
	}

}
