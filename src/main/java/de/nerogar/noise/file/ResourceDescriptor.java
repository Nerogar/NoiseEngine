package de.nerogar.noise.file;

import java.io.InputStream;

public interface ResourceDescriptor {

	InputStream asStream();

	ResourceDescriptor getRelative(String id);

	String getFilename();

}
