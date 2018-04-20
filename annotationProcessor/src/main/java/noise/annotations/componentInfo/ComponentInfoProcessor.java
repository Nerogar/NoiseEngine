package noise.annotations.componentInfo;

import noise.annotations.ComponentInfo;
import noise.annotations.ComponentSide;
import noise.annotations.componentParameter.ComponentParameterProcessor;
import noise.annotations.componentParameter.ComponentSerializeTemplate;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ComponentInfoProcessor {

	private final Messager                    messager;
	private       Path                        distLocation;
	private       ComponentParameterProcessor componentParameterProcessor;

	private OutputJsonContainer outputJsonContainer = new OutputJsonContainer();

	private class OutputJsonContainer {

		private Map<String, String> core   = new HashMap<>();
		private Map<String, String> client = new HashMap<>();
		private Map<String, String> server = new HashMap<>();
	}

	public ComponentInfoProcessor(Path distLocation, Messager messager, ComponentParameterProcessor componentParameterProcessor) {
		this.distLocation = distLocation;
		this.messager = messager;
		this.componentParameterProcessor = componentParameterProcessor;
	}

	public void processAnnotations(RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(ComponentInfo.class)) {
			TypeElement typeElement = (TypeElement) element;

			boolean hasEmptyConstructor = ElementFilter.constructorsIn(typeElement.getEnclosedElements())
					.stream().anyMatch(c -> c.getParameters().isEmpty());
			if (!hasEmptyConstructor) {
				messager.printMessage(Diagnostic.Kind.ERROR, "no default constructor", element);
			}

			ComponentInfo annotation = element.getAnnotation(ComponentInfo.class);
			String name = annotation.name();
			Name className = typeElement.getQualifiedName();

			Map<String, String> mapToPutIn
					= annotation.side() == ComponentSide.CORE ? outputJsonContainer.core
					: annotation.side() == ComponentSide.CLIENT ? outputJsonContainer.client
					: annotation.side() == ComponentSide.SERVER ? outputJsonContainer.server
					: outputJsonContainer.core;

			mapToPutIn.put(name, className.toString());

		}

	}

	public void createComponentsJson() {
		File componentsPath = new File(distLocation.toString() + "/data");
		File componentsFile = new File(distLocation.toString() + "/data/components.json");

		messager.printMessage(Diagnostic.Kind.NOTE, "creating components.json in path: " + componentsFile);

		if (!componentsPath.exists() && !componentsPath.mkdirs()) {
			messager.printMessage(Diagnostic.Kind.ERROR, "Could not create generated source directory: " + componentsPath);
		}
		try (OutputStream stream = new FileOutputStream(componentsFile)) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
			writer.write(toJson(outputJsonContainer));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			messager.printMessage(Diagnostic.Kind.ERROR, "Could not write components output file: " + e.getMessage());
		}
	}

	private void toJsonSingle(StringBuilder stringBuilder, Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String className = entry.getValue();
			String componentName = entry.getKey();

			if (componentParameterProcessor.containsClass(className)) {
				ComponentSerializeTemplate componentTemplate = componentParameterProcessor.getTemplate(className);
				className = componentTemplate.getPackage() + "." + componentTemplate.getClassname();
			}

			stringBuilder.append("        {\n");
			stringBuilder.append("            \"name\": \"").append(componentName).append("\",\n");
			stringBuilder.append("            \"class\": \"").append(className).append("\"\n");
			stringBuilder.append("        },\n");
		}
	}

	private String toJson(OutputJsonContainer outputJsonContainer) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\n");

		stringBuilder.append("    \"core\": [\n");
		toJsonSingle(stringBuilder, outputJsonContainer.core);
		stringBuilder.append("    ],\n");

		stringBuilder.append("    \"client\": [\n");
		toJsonSingle(stringBuilder, outputJsonContainer.client);
		stringBuilder.append("    ],\n");

		stringBuilder.append("    \"server\": [\n");
		toJsonSingle(stringBuilder, outputJsonContainer.server);
		stringBuilder.append("    ]\n");

		stringBuilder.append("\n}\n");
		return stringBuilder.toString();
	}

}
