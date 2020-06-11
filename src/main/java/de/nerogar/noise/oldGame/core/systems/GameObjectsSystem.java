package de.nerogar.noise.oldGame.core.systems;

import de.nerogar.noise.oldGame.*;
import de.nerogar.noise.serialization.*;
import de.nerogar.noise.util.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GameObjectsSystem extends SynchronizedSystem {

	private static final String CLIENT_LIST_NAME = "client";
	private static final String SERVER_LIST_NAME = "server";

	public static final String DEFAULT_NAME = "<default name>";

	private short MAX_ID = 0;

	private Map<String, Short>        idMap         = new HashMap<>();
	private Map<Short, String>        nameMap       = new HashMap<>();
	private Map<Short, NDSNodeObject> objectFileMap = new HashMap<>();

	protected Map<Short, List<Component>> blueprints = new HashMap<>();

	public short[] objectIDs;

	public GameObjectsSystem() {
		super("gameObjects");
	}

	@Override
	public void init() {
		if (checkSide(Side.SERVER)) {
			loadObjectsFromFolder(NoiseGame.DATA_DIR + "objects");
			initObjectIDs();
		}
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {
		NDSFile blueprintsFile = new NDSFile();

		// blueprints
		List<NDSNodeObject> blueprintList = getUniversalBlueprintList();
		NDSNodeObject[] blueprintArray = new NDSNodeObject[blueprintList.size()];
		for (int i = 0; i < blueprintList.size(); i++) {
			blueprintArray[i] = blueprintList.get(i);
		}
		blueprintsFile.getData().addObjectArray("blueprints", blueprintArray);

		// ids
		Map<String, Short> idMap = getIdMap();
		String[] nameArray = new String[idMap.size()];
		short[] idArray = new short[idMap.size()];
		int i = 0;
		for (Map.Entry<String, Short> entry : idMap.entrySet()) {
			nameArray[i] = entry.getKey();
			idArray[i] = entry.getValue();
			i++;
		}
		NDSNodeObject idMapObject = new NDSNodeObject();
		idMapObject.addStringUTF8Array("names", nameArray);
		idMapObject.addShortArray("ids", idArray);
		blueprintsFile.getData().addObject("idMap", idMapObject);

		NDSWriter.write(blueprintsFile, out);
	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {
		NDSFile blueprintsFile = NDSReader.read(in);

		List<NDSNodeObject> blueprintsNode = Arrays.asList(blueprintsFile.getData().getObjectArray("blueprints"));

		Map<String, Short> idMap = new HashMap<>();
		String[] nameArray = blueprintsFile.getData().getObject("idMap").getStringUTF8Array("names");
		short[] idArray = blueprintsFile.getData().getObject("idMap").getShortArray("ids");

		for (int i = 0; i < nameArray.length; i++) {
			idMap.put(nameArray[i], idArray[i]);
		}

		for (NDSNodeObject blueprint : blueprintsNode) {
			loadObject(idMap.get(blueprint.getStringUTF8("name")), blueprint);
		}

		initObjectIDs();

	}

	private short generateID() {
		return MAX_ID++;
	}

	public Map<String, Short> getIdMap() {
		return idMap;
	}

	public List<NDSNodeObject> getUniversalBlueprintList() {
		return new ArrayList<>(objectFileMap.values());
	}

	public short getID(String name) {
		return idMap.get(name);
	}

	public String getName(short id) {
		return nameMap.getOrDefault(id, DEFAULT_NAME);
	}

	public NDSNodeObject getObjectFile(short id) {
		return objectFileMap.get(id);
	}

	private void loadObjectsFromFolder(String folderName) {
		File rootFolder = new File(folderName);
		Queue<File> remainingFolders = new ArrayDeque<>();
		remainingFolders.add(rootFolder);

		while (!remainingFolders.isEmpty()) {
			File folder = remainingFolders.poll();

			File[] content = folder.listFiles();

			if (content == null) continue;

			for (File file : content) {
				if (file.isFile()) {
					try {
						loadObject(generateID(), NDSReader.readJsonFile(file.getAbsolutePath()).getData());
					} catch (FileNotFoundException e) {
						e.printStackTrace(NoiseGame.logger.getErrorStream());
					}
				} else if (file.isDirectory()) {
					remainingFolders.add(file);
				}
			}
		}

	}

	private void loadObject(short id, NDSNodeObject data) {
		String name = data.getStringUTF8("name");

		idMap.put(name, id);
		nameMap.put(id, name);
		objectFileMap.put(id, data);
	}

	private void initObjectIDs() {
		objectIDs = new short[nameMap.size()];

		int i = 0;
		for (short id : nameMap.keySet()) {
			objectIDs[i] = id;
			i++;
		}
	}

	private Component createComponent(short entityID, String componentName, NDSNodeObject data) {
		try {
			Component component = Components.getComponentClass(componentName).getConstructor().newInstance();
			component.setData(this, data);
			return component;
		} catch (NDSException e) {
			NoiseGame.logger.log(Logger.ERROR, "could not set data for component: " + componentName + " in entity " + getName(entityID));
			e.printStackTrace(NoiseGame.logger.getErrorStream());
			return null;
		} catch (NoSuchMethodException e) {
			NoiseGame.logger.log(Logger.ERROR, "could not find empty constructor for component: " + componentName);
			e.printStackTrace(NoiseGame.logger.getErrorStream());
			return null;
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace(NoiseGame.logger.getErrorStream());
			return null;
		}
	}

	private void addComponentToList(List<Component> components, Component newComponent) {
		if (newComponent == null) return;
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i).getClass() == newComponent.getClass()) {
				components.set(i, newComponent);
				return;
			}
		}
		components.add(newComponent);
	}

	private List<Component> buildComponentList(List<Component> components, String listName, String name) {
		return buildComponentList(components, listName, getID(name));
	}

	private List<Component> buildComponentList(List<Component> components, String listName, short entityID) {
		NDSNodeObject object = getObjectFile(entityID);
		NDSNodeObject componentsObject = object.getObject("components");

		if (componentsObject.contains("super")) {
			for (String superObject : componentsObject.getStringUTF8Array("super")) {
				buildComponentList(components, listName, superObject);
			}
		}

		for (NDSNodeObject componentNode : componentsObject.getObjectArray("core")) {
			Component newComponent = createComponent(entityID, componentNode.getStringUTF8("name"), componentNode.getObject("data"));
			addComponentToList(components, newComponent);
		}

		for (NDSNodeObject componentNode : componentsObject.getObjectArray(listName)) {
			Component newComponent = createComponent(entityID, componentNode.getStringUTF8("name"), componentNode.getObject("data"));
			addComponentToList(components, newComponent);
		}

		return components;
	}

	@SuppressWarnings("unchecked")
	public <C extends Component> C getComponentForEntity(short entityID, Class<C> componentClass) {
		List<Component> blueprint = getBlueprint(entityID);

		for (Component component : blueprint) {
			if (component.getClass() == componentClass) return (C) component;
		}

		return null;
	}

	public List<Component> getBlueprint(short entityID) {
		if (checkSide(Side.SERVER)) {
			return blueprints.computeIfAbsent(entityID, id -> buildComponentList(new ArrayList<>(), SERVER_LIST_NAME, entityID));
		} else if (checkSide(Side.CLIENT)) {
			return blueprints.computeIfAbsent(entityID, id -> buildComponentList(new ArrayList<>(), CLIENT_LIST_NAME, entityID));
		}

		return null;
	}

}
