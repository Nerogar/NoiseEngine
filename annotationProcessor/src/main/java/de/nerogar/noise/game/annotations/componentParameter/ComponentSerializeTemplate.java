package de.nerogar.noise.game.annotations.componentParameter;

import de.nerogar.noise.game.annotations.ClassTemplate;

public class ComponentSerializeTemplate extends ClassTemplate {

	private final String dataparameterName = "data";

	private String superClassName;
	private String className;
	private String packageName;

	private StringBuilder setDataSB;
	private StringBuilder saveSB;
	private StringBuilder copySB;

	public ComponentSerializeTemplate(String superClassName, String packageName) {
		this.superClassName = superClassName;
		this.className = superClassName + "Generated";
		this.packageName = packageName;

		setDataSB = new StringBuilder();
		saveSB = new StringBuilder();
		copySB = new StringBuilder();
	}

	@Override
	public String getPackage() { return packageName; }

	@Override
	public String[] getImports() {
		return new String[] {
				"de.nerogar.noise.serialization.NDSNodeObject",
				"de.nerogar.noise.game.core.systems.GameObjectsSystem",
				"de.nerogar.noise.game.Component"
		};
	}

	@Override
	public String getClassname() { return className; }

	@Override
	public String getSuperClassname() {
		return superClassName;
	}

	public void addInt(String name) {
		setDataSB.append("\t\t").append(name).append(" = data.getInt(\"").append(name).append("\");\n");
		saveSB.append("\t\t").append("data.addInt(\"").append(name).append("\", ").append(name).append(");\n");
		copySB.append("\t\t").append(name).append(" = other.").append(name).append(";\n");
	}

	public void addFloat(String name) {
		setDataSB.append("\t\t").append(name).append(" = data.getFloat(\"").append(name).append("\");\n");
		saveSB.append("\t\t").append("data.addFloat(\"").append(name).append("\", ").append(name).append(");\n");
		copySB.append("\t\t").append(name).append(" = other.").append(name).append(";\n");
	}

	public void addBoolean(String name) {
		setDataSB.append("\t\t").append(name).append(" = data.getBoolean(\"").append(name).append("\");\n");
		saveSB.append("\t\t").append("data.addBoolean(\"").append(name).append("\", ").append(name).append(");\n");
		copySB.append("\t\t").append(name).append(" = other.").append(name).append(";\n");
	}

	public void addString(String name) {
		setDataSB.append("\t\t").append(name).append(" = data.getStringUTF8(\"").append(name).append("\");\n");
		saveSB.append("\t\t").append("data.addStringUTF8(\"").append(name).append("\", ").append(name).append(");\n");
		copySB.append("\t\t").append(name).append(" = other.").append(name).append(";\n");
	}

	private StringBuilder createSetData() {
		StringBuilder sb = new StringBuilder();

		sb.append("\t@Override\n");
		sb.append("\tpublic void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {\n");
		sb.append("\t\tsuper.setData(gameObjectsSystem, data);\n");

		sb.append(setDataSB);

		sb.append("\t}\n");

		return sb;
	}

	private StringBuilder createSave() {
		StringBuilder sb = new StringBuilder();

		sb.append("\t@Override\n");
		sb.append("\tpublic void save(NDSNodeObject data) {\n");
		sb.append("\t\tsuper.save(data);\n");

		sb.append(saveSB);

		sb.append("\t}\n");

		return sb;
	}

	private StringBuilder createNewInstance() {
		StringBuilder sb = new StringBuilder();

		sb.append("\t@Override\n");
		sb.append("\tpublic ").append(getSuperClassname()).append(" newInstance() {\n");
		sb.append("\t\treturn new ").append(getClassname()).append("();\n");
		sb.append("\t}\n");

		return sb;
	}

	private StringBuilder createCopyFrom() {
		StringBuilder sb = new StringBuilder();

		sb.append("\t@Override\n");
		sb.append("\tpublic void copyFrom(").append(getSuperClassname()).append(" other) {\n");
		sb.append("\t\tsuper.copyFrom(other);\n");

		sb.append(copySB);

		sb.append("\t}\n");

		return sb;
	}

	@Override
	protected String createClass() {
		StringBuilder sb = new StringBuilder();
		sb.append(createSetData()).append("\n");
		sb.append(createSave()).append("\n");
		sb.append(createNewInstance()).append("\n");
		sb.append(createCopyFrom()).append("\n");
		return createClass(sb);
	}
}
