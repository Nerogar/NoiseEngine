package de.nerogar.noise.game.annotations.componentParameter;

import de.nerogar.noise.game.annotations.ClassWriter;
import de.nerogar.noise.game.annotations.ComponentParameter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentParameterProcessor {

	private final Filer    filer;
	private final Messager messager;

	private Map<String, ComponentSerializeTemplate> classTemplateMap = new HashMap<>();

	public ComponentParameterProcessor(Filer filer, Messager messager) {
		this.filer = filer;
		this.messager = messager;
	}

	private ComponentSerializeTemplate getTemplate(String fullClassName, String simpleClassName) {
		return classTemplateMap.computeIfAbsent(fullClassName, s -> new ComponentSerializeTemplate(simpleClassName));
	}

	public ComponentSerializeTemplate getTemplate(String fullClassName) {
		return classTemplateMap.get(fullClassName);
	}

	public void processAnnotations(RoundEnvironment roundEnv) {
		Set<VariableElement> fields = ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(ComponentParameter.class));

		for (VariableElement field : fields) {
			TypeElement enclosingClass = (TypeElement) field.getEnclosingElement();
			String fullClassName = enclosingClass.getQualifiedName().toString();
			String simpleClassName = field.getEnclosingElement().getSimpleName().toString();
			String fieldName = field.getSimpleName().toString();

			ComponentSerializeTemplate componentSerializeTemplate = getTemplate(fullClassName, simpleClassName);

			TypeMirror typeMirror = field.asType();
			TypeKind kind = typeMirror.getKind();

			if (!(field.getModifiers().contains(Modifier.PUBLIC) || field.getModifiers().contains(Modifier.PROTECTED))) {
				messager.printMessage(Diagnostic.Kind.ERROR, fieldName + " in class " + fullClassName + " needs access modifier public or protected!", field);
			}

			if (kind.isPrimitive()) {
				if (kind == TypeKind.INT) {
					componentSerializeTemplate.addInt(fieldName);
				} else if (kind == TypeKind.FLOAT) {
					componentSerializeTemplate.addFloat(fieldName);
				} else if (kind == TypeKind.BOOLEAN) {
					componentSerializeTemplate.addBoolean(fieldName);
				}
			} else {
				if (kind == TypeKind.DECLARED) {
					if (typeMirror.toString().equals("java.lang.String")) {
						componentSerializeTemplate.addString(fieldName);
					}
				}
			}

		}

	}

	public boolean containsClass(String className) {
		return classTemplateMap.containsKey(className);
	}

	public void writeClasses() {
		ClassWriter classWriter = new ClassWriter(filer);
		for (ComponentSerializeTemplate componentSerializeTemplate : classTemplateMap.values()) {
			classWriter.writeClass(messager, componentSerializeTemplate);
		}
	}

}
