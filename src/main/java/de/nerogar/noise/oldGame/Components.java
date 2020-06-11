package de.nerogar.noise.oldGame;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// todo Components class cannot have static methods, because server and client will access the same memory, creating concurrent modification exceptions
public class Components {

	private static Map<String, String>                     classNames       = new HashMap<>();
	private static Map<String, Class<? extends Component>> componentClasses = new HashMap<>();

	private static List<Class<? extends Component>>       componentFromID = new ArrayList<>();
	private static Map<Class<? extends Component>, Short> idFromComponent = new HashMap<>();

	public static void init() {
		initFile(FileUtil.get("data/components.json").asStream());
		initFile(FileUtil.get("<components.json>", FileUtil.DATA_SUBFOLDER).asStream());
	}

	private static void initFile(InputStream inputStream) {
		NDSFile file = NDSReader.readJson(new BufferedReader(new InputStreamReader(inputStream)));

		NDSNodeObject[] coreArray = file.getData().getObjectArray("core");
		NDSNodeObject[] serverArray = file.getData().getObjectArray("server");
		NDSNodeObject[] clientArray = file.getData().getObjectArray("client");

		initArray(coreArray);
		initArray(serverArray);
		initArray(clientArray);

		// init IDs for core components
		for (NDSNodeObject ndsNodeObject : coreArray) {
			String componentName = ndsNodeObject.getStringUTF8("name");
			Class<? extends Component> componentClass = getComponentClass(componentName);

			componentFromID.add(componentClass);
			idFromComponent.put(componentClass, (short) (componentFromID.size() - 1));
		}
	}

	private static void initArray(NDSNodeObject[] array) {
		for (NDSNodeObject ndsNodeObject : array) {
			classNames.put(
					ndsNodeObject.getStringUTF8("name"),
					ndsNodeObject.getStringUTF8("class")
			              );

		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Component> initSingle(String name) {
		try {
			return (Class<? extends Component>) Class.forName(classNames.get(name));
		} catch (ClassNotFoundException e) {
			e.printStackTrace(NoiseGame.logger.getErrorStream());
			return null;
		}
	}

	public static Class<? extends Component> getComponentClass(String name) {
		return componentClasses.computeIfAbsent(name, Components::initSingle);
	}

	public static Class<? extends Component> getComponentByID(short componentID) {
		return componentFromID.get(componentID);
	}

	public static short getIDFromComponent(Class<? extends Component> componentClass) {
		return idFromComponent.get(componentClass);
	}

}
