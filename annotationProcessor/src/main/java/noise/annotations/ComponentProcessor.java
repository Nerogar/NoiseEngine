package noise.annotations;

import noise.annotations.componentInfo.ComponentInfoProcessor;
import noise.annotations.componentParameter.ComponentParameterProcessor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@SupportedAnnotationTypes({ "noise.annotations.ComponentInfo", "noise.annotations.ComponentParameter" })
@SupportedSourceVersion(SourceVersion.RELEASE_10)
public class ComponentProcessor extends AbstractProcessor {

	private Messager messager;
	private Filer    filer;
	private Path     distLocation;

	private ComponentParameterProcessor componentParameterProcessor;
	private ComponentInfoProcessor      componentInfoProcessor;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		messager = processingEnv.getMessager();
		filer = processingEnv.getFiler();
		distLocation = createDistLocation();

		componentParameterProcessor = new ComponentParameterProcessor(filer, messager);
		componentInfoProcessor = new ComponentInfoProcessor(distLocation, messager, componentParameterProcessor);
	}

	private Path createDistLocation() {
		try {
			FileObject resource = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "tmp");

			Path projectPath = Paths.get(resource.toUri())
					// root/src/main/java/tmp
					.getParent()  // root/src/main/java
					.getParent()  // root/src/main
					.getParent()  // root/src
					.getParent(); // root
			resource.delete();
			distLocation = projectPath.resolve("src-gen/main/dist");

			return distLocation;
		} catch (IOException e) {
			e.printStackTrace();
			messager.printMessage(Diagnostic.Kind.ERROR, "could not determine project root directory");

			return null;
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		componentParameterProcessor.processAnnotations(roundEnv);
		componentInfoProcessor.processAnnotations(roundEnv);

		componentParameterProcessor.writeClasses();

		if (roundEnv.processingOver()) {
			componentInfoProcessor.createComponentsJson();
		}

		return true;
	}

}
