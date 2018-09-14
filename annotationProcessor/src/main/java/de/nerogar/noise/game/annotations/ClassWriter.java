package de.nerogar.noise.game.annotations;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ClassWriter {

	private Filer filer;

	public ClassWriter(Filer filer) {
		this.filer = filer;
	}

	public void writeClass(Messager messager, ClassTemplate template) {
		if (template.isWritten) return;
		template.isWritten = true;

		messager.printMessage(Diagnostic.Kind.NOTE, "writing class: " + template.getClassname());

		JavaFileObject sourceFile = null;
		try {
			sourceFile = filer.createSourceFile(template.getPackage() + "." + template.getClassname());
		} catch (IOException e) {
			messager.printMessage(Diagnostic.Kind.ERROR, "could not create source file for class: " + template.getClassname());
			messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
			return;
		}

		try (BufferedWriter writer = new BufferedWriter(sourceFile.openWriter())) {
			writeClass(writer, template);
		} catch (FileNotFoundException e) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Could not write class to file: " + template.getClassname());
			messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());

			return;
		} catch (IOException e) {
			messager.printMessage(Diagnostic.Kind.ERROR, "IOException while writing class to file: " + template.getClassname());
			messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());

			return;
		}
	}

	private void writeClass(BufferedWriter writer, ClassTemplate template) throws IOException {
		writer.write(template.createClass());
	}

}
