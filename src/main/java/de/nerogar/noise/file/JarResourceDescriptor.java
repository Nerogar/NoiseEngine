package de.nerogar.noise.file;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

import java.io.File;
import java.io.InputStream;

public class JarResourceDescriptor implements ResourceDescriptor {

	private String filename;
	private String subFolder;

	private String folder;

	public JarResourceDescriptor(String id, String subFolder) {
		this.filename = FileUtil.getCanonicalPath(id);
		this.subFolder = subFolder;

		folder = filename.substring(0, filename.lastIndexOf('/'));
	}

	@Override
	public InputStream asStream() {
		InputStream inputStream = JarResourceDescriptor.class.getResourceAsStream(filename);

		if (inputStream == null) {
			Noise.getLogger().log(Logger.WARNING, "could not load resource from jar: " + filename);
		}

		return inputStream;
	}

	@Override
	public ResourceDescriptor getRelative(String id) {
		if (id.startsWith("<") || id.startsWith("(")) return FileUtil.get(id, subFolder);

		String parent = new File(filename).getParent();
		while (id.startsWith("../")) {
			id = id.substring("../".length());
			parent = new File(parent).getParent();
		}

		return new JarResourceDescriptor(parent + "/" + id, subFolder);
	}

	@Override
	public String getFilename() {
		return filename;
	}

}
