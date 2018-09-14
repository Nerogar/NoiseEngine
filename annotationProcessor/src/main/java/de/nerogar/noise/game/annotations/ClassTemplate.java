package de.nerogar.noise.game.annotations;

public abstract class ClassTemplate {

	protected boolean isWritten;

	public abstract String getPackage();

	public abstract String[] getImports();

	public abstract String getClassname();

	public String getSuperClassname() {
		return null;
	}

	protected abstract String createClass();

	protected final String createClass(StringBuilder body) {

		StringBuilder classSB = new StringBuilder();

		classSB.append("package ").append(getPackage()).append(";\n");
		classSB.append('\n');

		for (String importName : getImports()) {
			classSB.append("import ").append(importName).append(";\n");
		}
		classSB.append('\n');

		classSB.append("public class ").append(getClassname());
		if (getSuperClassname() != null) classSB.append(" extends ").append(getSuperClassname()).append(" ");
		classSB.append("{\n");
		classSB.append('\n');

		classSB.append(body);

		classSB.append("}\n");

		return classSB.toString();
	}

}
